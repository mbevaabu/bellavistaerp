package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.dto.PurchaseOrderCreateRequest;
import com.replica.bellavistaerp.procurement.dto.PurchaseOrderLineRequest;
import com.replica.bellavistaerp.procurement.entity.PurchaseOrder;
import com.replica.bellavistaerp.procurement.entity.PurchaseOrderLine;
import com.replica.bellavistaerp.procurement.entity.PurchaseRequest;
import com.replica.bellavistaerp.procurement.entity.PurchaseRequestLine;
import com.replica.bellavistaerp.procurement.enums.PurchaseOrderStatus;
import com.replica.bellavistaerp.procurement.enums.PurchaseRequestStatus;
import com.replica.bellavistaerp.procurement.repository.PurchaseOrderRepository;
import com.replica.bellavistaerp.procurement.repository.PurchaseRequestRepository;
import com.replica.bellavistaerp.procurement.service.PurchaseOrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;

    // ---------------------------------------------------------
    // CREATE PURCHASE ORDER
    // ---------------------------------------------------------

    @Override
    public PurchaseOrder create(PurchaseOrderCreateRequest request) {

        if (request == null || request.getPurchaseRequestId() == null) {
            throw new IllegalArgumentException(
                    "Purchase Request ID is required."
            );
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one Purchase Order line is required."
            );
        }

        PurchaseRequest purchaseRequest = purchaseRequestRepository
                .findById(request.getPurchaseRequestId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Purchase Request not found."
                ));

        if (purchaseRequest.getStatus() != PurchaseRequestStatus.APPROVED
                && purchaseRequest.getStatus()
                != PurchaseRequestStatus.CONVERTED_TO_PO) {

            throw new IllegalStateException(
                    "Only approved Purchase Requests can generate Purchase Orders."
            );
        }

        Map<Long, PurchaseRequestLine> requestLines = new HashMap<>();

        for (PurchaseRequestLine line : purchaseRequest.getItems()) {
            requestLines.put(line.getId(), line);
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();

        purchaseOrder.setPurchaseRequest(purchaseRequest);
        purchaseOrder.setSupplierName(request.getSupplierName());
        purchaseOrder.setSupplierContact(request.getSupplierContact());
        purchaseOrder.setSupplierAddress(request.getSupplierAddress());
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setExpectedDeliveryDate(
                request.getExpectedDeliveryDate()
        );
        purchaseOrder.setDeliveryLocation(request.getDeliveryLocation());
        purchaseOrder.setPaymentTerms(request.getPaymentTerms());
        purchaseOrder.setRemarks(request.getRemarks());
        purchaseOrder.setPreparedBy(request.getPreparedBy());
        purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);

        BigDecimal subTotal = BigDecimal.ZERO;

        Set<Long> processedLines = new HashSet<>();

        for (PurchaseOrderLineRequest lineRequest : request.getItems()) {

            if (lineRequest.getPurchaseRequestLineId() == null
                    || lineRequest.getQuantity() == null
                    || lineRequest.getUnitPrice() == null) {

                throw new IllegalArgumentException(
                        "Purchase Request line, quantity and unit price are required."
                );
            }

            if (lineRequest.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "Ordered quantity must be greater than zero."
                );
            }

            if (lineRequest.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Unit price cannot be negative."
                );
            }

            Long requestLineId = lineRequest.getPurchaseRequestLineId();

            if (!processedLines.add(requestLineId)) {
                throw new IllegalArgumentException(
                        "A Purchase Request line cannot appear twice in one PO."
                );
            }

            PurchaseRequestLine requestLine = requestLines.get(requestLineId);

            if (requestLine == null) {
                throw new IllegalArgumentException(
                        "The selected line does not belong to this Purchase Request."
                );
            }

            BigDecimal alreadyOrdered = calculateAlreadyOrdered(
                    requestLineId
            );

            BigDecimal remainingQuantity = requestLine.getQuantity()
                    .subtract(alreadyOrdered);

            if (lineRequest.getQuantity().compareTo(remainingQuantity) > 0) {
                throw new IllegalArgumentException(
                        "Ordered quantity exceeds the remaining approved quantity "
                                + "for Purchase Request line " + requestLineId
                );
            }

            PurchaseOrderLine purchaseOrderLine = new PurchaseOrderLine();

            purchaseOrderLine.setItem(requestLine.getItem());
            purchaseOrderLine.setPurchaseRequestLine(requestLine);
            purchaseOrderLine.setQuantity(lineRequest.getQuantity());
            purchaseOrderLine.setUnitOfMeasure(
                    requestLine.getUnitOfMeasure()
            );
            purchaseOrderLine.setUnitPrice(lineRequest.getUnitPrice());
            purchaseOrderLine.setRemarks(lineRequest.getRemarks());

            BigDecimal lineTotal = lineRequest.getQuantity()
                    .multiply(lineRequest.getUnitPrice());

            purchaseOrderLine.setLineTotal(lineTotal);

            purchaseOrder.addItem(purchaseOrderLine);

            subTotal = subTotal.add(lineTotal);
        }

        BigDecimal taxAmount = request.getTaxAmount();

        if (taxAmount == null || taxAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Tax amount cannot be negative or null."
            );
        }

        purchaseOrder.setSubTotal(subTotal);
        purchaseOrder.setTaxAmount(taxAmount);
        purchaseOrder.setTotalAmount(subTotal.add(taxAmount));

        String orderNumber = "BVC-PO-"
                + Year.now().getValue()
                + "-"
                + String.format(
                        "%05d",
                        purchaseOrderRepository.count() + 1
                );

        purchaseOrder.setOrderNumber(orderNumber);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

        purchaseRequest.setStatus(PurchaseRequestStatus.CONVERTED_TO_PO);
        purchaseRequestRepository.save(purchaseRequest);

        return savedOrder;
    }

    // ---------------------------------------------------------
    // RETRIEVE PURCHASE ORDERS
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findAll() {

        return purchaseOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder findById(Long id) {

        return getPurchaseOrder(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder findByOrderNumber(String orderNumber) {

        return purchaseOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Purchase Order not found: " + orderNumber
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findByPurchaseRequest(Long purchaseRequestId) {

        return purchaseOrderRepository.findByPurchaseRequest_Id(
                purchaseRequestId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findByStatus(PurchaseOrderStatus status) {

        return purchaseOrderRepository.findByStatus(status);
    }

    // ---------------------------------------------------------
    // ISSUE PURCHASE ORDER
    // ---------------------------------------------------------

    @Override
    public PurchaseOrder issuePurchaseOrder(
            Long purchaseOrderId,
            String issuedBy) {

        PurchaseOrder purchaseOrder = getPurchaseOrder(purchaseOrderId);

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only draft Purchase Orders can be issued."
            );
        }

        if (issuedBy == null || issuedBy.isBlank()) {
            throw new IllegalArgumentException(
                    "The name of the issuing officer is required."
            );
        }

        purchaseOrder.setIssuedBy(issuedBy);
        purchaseOrder.setIssuedDate(LocalDateTime.now());
        purchaseOrder.setStatus(PurchaseOrderStatus.ISSUED);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    // ---------------------------------------------------------
    // CANCEL PURCHASE ORDER
    // ---------------------------------------------------------

    @Override
    public PurchaseOrder cancelPurchaseOrder(
            Long purchaseOrderId,
            String cancelledBy,
            String reason) {

        PurchaseOrder purchaseOrder = getPurchaseOrder(purchaseOrderId);

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only draft Purchase Orders can be cancelled directly."
            );
        }

        if (cancelledBy == null || cancelledBy.isBlank()) {
            throw new IllegalArgumentException(
                    "The cancelling officer is required."
            );
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "A cancellation reason is required."
            );
        }

        purchaseOrder.setRemarks(
                "Cancelled by " + cancelledBy + ". Reason: " + reason
        );

        purchaseOrder.setStatus(PurchaseOrderStatus.CANCELLED);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    // ---------------------------------------------------------
    // HELPER METHODS
    // ---------------------------------------------------------

    private BigDecimal calculateAlreadyOrdered(Long requestLineId) {

        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        BigDecimal totalOrdered = BigDecimal.ZERO;

        for (PurchaseOrder order : orders) {

            if (order.getStatus() == PurchaseOrderStatus.CANCELLED) {
                continue;
            }

            for (PurchaseOrderLine line : order.getItems()) {

                if (line.getPurchaseRequestLine().getId()
                        .equals(requestLineId)) {

                    totalOrdered = totalOrdered.add(line.getQuantity());
                }
            }
        }

        return totalOrdered;
    }

    private PurchaseOrder getPurchaseOrder(Long id) {

        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Purchase Order not found with ID: " + id
                ));
    }
}