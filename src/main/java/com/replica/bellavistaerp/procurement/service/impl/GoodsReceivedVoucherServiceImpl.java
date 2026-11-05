package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.dto.GoodsReceivedVoucherCreateRequest;
import com.replica.bellavistaerp.procurement.dto.GoodsReceivedVoucherLineRequest;

import com.replica.bellavistaerp.procurement.entity.GoodsReceivedVoucher;
import com.replica.bellavistaerp.procurement.entity.GoodsReceivedVoucherLine;
import com.replica.bellavistaerp.procurement.entity.PurchaseOrder;
import com.replica.bellavistaerp.procurement.entity.PurchaseOrderLine;
import com.replica.bellavistaerp.procurement.entity.StockLocation;

import com.replica.bellavistaerp.procurement.enums.GoodsReceivedVoucherStatus;
import com.replica.bellavistaerp.procurement.enums.PurchaseOrderStatus;

import com.replica.bellavistaerp.procurement.repository.GoodsReceivedVoucherLineRepository;
import com.replica.bellavistaerp.procurement.repository.GoodsReceivedVoucherRepository;
import com.replica.bellavistaerp.procurement.repository.PurchaseOrderLineRepository;
import com.replica.bellavistaerp.procurement.repository.PurchaseOrderRepository;
import com.replica.bellavistaerp.procurement.repository.StockLocationRepository;

import com.replica.bellavistaerp.procurement.service.GoodsReceivedVoucherService;
import com.replica.bellavistaerp.procurement.service.StockMovementService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsReceivedVoucherServiceImpl implements GoodsReceivedVoucherService {

    private final GoodsReceivedVoucherRepository goodsReceivedVoucherRepository;

    private final GoodsReceivedVoucherLineRepository goodsReceivedVoucherLineRepository;

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    private final StockLocationRepository stockLocationRepository;

    private final StockMovementService stockMovementService;


    // ---------------------------------------------------------
    // CREATE GRV
    // ---------------------------------------------------------

    @Override
    public GoodsReceivedVoucher create(GoodsReceivedVoucherCreateRequest request) {

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(
                request.getPurchaseOrderId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Purchase Order not found.")
        );

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.ISSUED
                && purchaseOrder.getStatus() != PurchaseOrderStatus.PARTIALLY_RECEIVED) {

            throw new IllegalStateException(
                    "A GRV can only be created against an issued or partially received Purchase Order."
            );
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "A GRV must contain at least one delivery line."
            );
        }

        List<PurchaseOrderLine> purchaseOrderLines =
                purchaseOrderLineRepository.findByPurchaseOrder_Id(
                        purchaseOrder.getId()
                );

        Map<Long, PurchaseOrderLine> orderLineMap = purchaseOrderLines.stream()
                .collect(Collectors.toMap(
                        PurchaseOrderLine::getId,
                        Function.identity()
                ));

        Set<Long> processedOrderLineIds = new HashSet<>();

        GoodsReceivedVoucher voucher = new GoodsReceivedVoucher();

        voucher.setGrvNumber(generateGrvNumber());
        voucher.setPurchaseOrder(purchaseOrder);
        voucher.setSupplierName(purchaseOrder.getSupplierName());

        voucher.setCreatedBy(request.getCreatedBy().trim());
        voucher.setReceivedBy(request.getReceivedBy().trim());

        voucher.setDeliveryNoteNumber(request.getDeliveryNoteNumber());
        voucher.setDeliveryDate(request.getDeliveryDate());
        voucher.setVehicleRegistration(request.getVehicleRegistration());
        voucher.setDriverName(request.getDriverName());
        voucher.setDeliveryLocation(request.getDeliveryLocation());
        voucher.setRemarks(request.getRemarks());

        voucher.setReceivedDate(LocalDateTime.now());
        voucher.setStatus(GoodsReceivedVoucherStatus.DRAFT);

        // -----------------------------------------------------
        // PROCESS DELIVERY LINES
        // -----------------------------------------------------

        for (GoodsReceivedVoucherLineRequest lineRequest : request.getItems()) {

            Long purchaseOrderLineId = lineRequest.getPurchaseOrderLineId();

            if (!processedOrderLineIds.add(purchaseOrderLineId)) {
                throw new IllegalArgumentException(
                        "Duplicate Purchase Order line detected in the GRV."
                );
            }

            PurchaseOrderLine orderLine = orderLineMap.get(purchaseOrderLineId);

            if (orderLine == null) {
                throw new IllegalArgumentException(
                        "The selected Purchase Order line does not belong to this Purchase Order."
                );
            }

            BigDecimal delivered = lineRequest.getQuantityDelivered();
            BigDecimal accepted = lineRequest.getQuantityAccepted();
            BigDecimal rejected = lineRequest.getQuantityRejected();
            BigDecimal damaged = lineRequest.getQuantityDamaged();

            validateDeliveryQuantities(delivered, accepted, rejected, damaged);

            // -------------------------------------------------
            // CHECK PREVIOUSLY ACCEPTED QUANTITIES
            // -------------------------------------------------

            BigDecimal previouslyReceived =
                    getPreviouslyAcceptedQuantity(orderLine.getId());

            BigDecimal orderedQuantity = orderLine.getQuantity();

            BigDecimal outstandingQuantity = orderedQuantity
                    .subtract(previouslyReceived);

            if (delivered.compareTo(outstandingQuantity) > 0) {
                throw new IllegalArgumentException(
                        "Delivered quantity exceeds the outstanding quantity for item: "
                                + orderLine.getItem().getName()
                );
            }

            // -------------------------------------------------
            // STOCK LOCATION
            // -------------------------------------------------

            StockLocation stockLocation = stockLocationRepository.findById(
                    lineRequest.getStockLocationId()
            ).orElseThrow(() ->
                    new IllegalArgumentException(
                            "Stock location not found: "
                                    + lineRequest.getStockLocationId()
                    )
            );

            // -------------------------------------------------
            // CREATE GRV LINE
            // -------------------------------------------------

            GoodsReceivedVoucherLine voucherLine =
                    new GoodsReceivedVoucherLine();

            voucherLine.setPurchaseOrderLine(orderLine);
            voucherLine.setItem(orderLine.getItem());
            voucherLine.setStockLocation(stockLocation);

            voucherLine.setQuantityOrdered(orderedQuantity);
            voucherLine.setQuantityPreviouslyReceived(previouslyReceived);

            voucherLine.setQuantityDelivered(delivered);
            voucherLine.setQuantityAccepted(accepted);
            voucherLine.setQuantityRejected(rejected);
            voucherLine.setQuantityDamaged(damaged);

            voucherLine.setQuantityOutstanding(
                    outstandingQuantity.subtract(accepted)
            );

            voucherLine.setUnitOfMeasure(orderLine.getUnitOfMeasure());

            voucherLine.setBatchNumber(lineRequest.getBatchNumber());
            voucherLine.setExpiryDate(lineRequest.getExpiryDate());
            voucherLine.setInspectionRemarks(
                    lineRequest.getInspectionRemarks()
            );
            voucherLine.setRejectionReason(
                    lineRequest.getRejectionReason()
            );

            voucher.addItem(voucherLine);
        }

        return goodsReceivedVoucherRepository.save(voucher);
    }


    // ---------------------------------------------------------
    // RETRIEVE ALL GRVs
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceivedVoucher> findAll() {

        return goodsReceivedVoucherRepository.findAll();
    }


    // ---------------------------------------------------------
    // FIND GRV BY ID
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public GoodsReceivedVoucher findById(Long id) {

        return getVoucher(id);
    }


    // ---------------------------------------------------------
    // FIND GRV BY NUMBER
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public GoodsReceivedVoucher findByGrvNumber(String grvNumber) {

        return goodsReceivedVoucherRepository.findByGrvNumber(grvNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "GRV not found: " + grvNumber
                        )
                );
    }


    // ---------------------------------------------------------
    // FIND GRVs BY PURCHASE ORDER
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceivedVoucher> findByPurchaseOrder(Long purchaseOrderId) {

        return goodsReceivedVoucherRepository
                .findByPurchaseOrder_IdOrderByReceivedDateDesc(purchaseOrderId);
    }


    // ---------------------------------------------------------
    // FIND GRVs BY STATUS
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceivedVoucher> findByStatus(
            GoodsReceivedVoucherStatus status) {

        return goodsReceivedVoucherRepository.findByStatus(status);
    }


    // ---------------------------------------------------------
    // SUBMIT GRV FOR STOCK VERIFICATION
    // ---------------------------------------------------------

    @Override
    public GoodsReceivedVoucher submitForVerification(
            Long grvId,
            String submittedBy) {

        GoodsReceivedVoucher voucher = getVoucher(grvId);

        if (voucher.getStatus() != GoodsReceivedVoucherStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only a draft GRV can be submitted for verification."
            );
        }

        requireName(submittedBy, "Submitted by");

        voucher.setSubmittedBy(submittedBy.trim());
        voucher.setSubmissionDate(LocalDateTime.now());

        voucher.setStatus(
                GoodsReceivedVoucherStatus.PENDING_STOCK_VERIFICATION
        );

        return goodsReceivedVoucherRepository.save(voucher);
    }


    // ---------------------------------------------------------
    // STOCK CONTROLLER VERIFICATION
    // ---------------------------------------------------------

    @Override
    public GoodsReceivedVoucher verifyByStockController(
            Long grvId,
            String stockController,
            String remarks) {

        GoodsReceivedVoucher voucher = getVoucher(grvId);

        if (voucher.getStatus()
                != GoodsReceivedVoucherStatus.PENDING_STOCK_VERIFICATION) {

            throw new IllegalStateException(
                    "This GRV is not awaiting Stock Controller verification."
            );
        }

        requireName(stockController, "Stock Controller");

        voucher.setVerifiedBy(stockController.trim());
        voucher.setVerificationDate(LocalDateTime.now());
        voucher.setVerificationRemarks(remarks);

        voucher.setStatus(
                GoodsReceivedVoucherStatus.PENDING_OM_APPROVAL
        );

        return goodsReceivedVoucherRepository.save(voucher);
    }


    // ---------------------------------------------------------
    // OPERATIONS MANAGER APPROVAL
    // ---------------------------------------------------------

    @Override
    public GoodsReceivedVoucher approveByOperationsManager(
            Long grvId,
            String operationsManager,
            String comments) {

        GoodsReceivedVoucher voucher = getVoucher(grvId);

        if (voucher.getStatus()
                != GoodsReceivedVoucherStatus.PENDING_OM_APPROVAL) {

            throw new IllegalStateException(
                    "This GRV is not awaiting Operations Manager approval."
            );
        }

        requireName(operationsManager, "Operations Manager");

        // The Operations Manager must be independent of the
        // person who created, received, submitted or verified the GRV.

        if (samePerson(operationsManager, voucher.getCreatedBy())
                || samePerson(operationsManager, voucher.getReceivedBy())
                || samePerson(operationsManager, voucher.getSubmittedBy())
                || samePerson(operationsManager, voucher.getVerifiedBy())) {

            throw new IllegalArgumentException(
                    "The Operations Manager approving this GRV must be independent "
                            + "of the GRV preparation and verification process."
            );
        }

        voucher.setApprovedBy(operationsManager.trim());
        voucher.setApprovalDate(LocalDateTime.now());
        voucher.setApprovalComments(comments);

        BigDecimal totalAccepted = BigDecimal.ZERO;
        BigDecimal totalRejected = BigDecimal.ZERO;

        for (GoodsReceivedVoucherLine line : voucher.getItems()) {

            totalAccepted = totalAccepted.add(line.getQuantityAccepted());
            totalRejected = totalRejected.add(line.getQuantityRejected());
        }

        // -----------------------------------------------------
        // DETERMINE ACCEPTANCE STATUS
        // -----------------------------------------------------

        if (totalAccepted.compareTo(BigDecimal.ZERO) > 0
                && totalRejected.compareTo(BigDecimal.ZERO) > 0) {

            voucher.setStatus(
                    GoodsReceivedVoucherStatus.PARTIALLY_ACCEPTED
            );

        } else if (totalAccepted.compareTo(BigDecimal.ZERO) > 0) {

            voucher.setStatus(
                    GoodsReceivedVoucherStatus.FULLY_ACCEPTED
            );

        } else {

            voucher.setStatus(
                    GoodsReceivedVoucherStatus.REJECTED
            );
        }

        goodsReceivedVoucherRepository.save(voucher);

        // -----------------------------------------------------
        // POST ACCEPTED QUANTITIES INTO STOCK
        // -----------------------------------------------------

        for (GoodsReceivedVoucherLine line : voucher.getItems()) {

            if (line.getQuantityAccepted()
                    .compareTo(BigDecimal.ZERO) > 0) {

                stockMovementService.receiveStock(
                        line.getItem().getId(),
                        line.getStockLocation().getId(),
                        line.getQuantityAccepted(),
                        voucher.getGrvNumber(),
                        "Accepted delivery against Purchase Order "
                                + voucher.getPurchaseOrder().getOrderNumber()
                                + ". Approved by "
                                + operationsManager,
                        voucher.getVerifiedBy()
                );
            }
        }

        // -----------------------------------------------------
        // UPDATE PURCHASE ORDER RECEIVING STATUS
        // -----------------------------------------------------

        updatePurchaseOrderStatus(voucher.getPurchaseOrder());

        return voucher;
    }


    // ---------------------------------------------------------
    // OPERATIONS MANAGER REJECTION
    // ---------------------------------------------------------

    @Override
    public GoodsReceivedVoucher rejectByOperationsManager(
            Long grvId,
            String operationsManager,
            String reason) {

        GoodsReceivedVoucher voucher = getVoucher(grvId);

        if (voucher.getStatus()
                != GoodsReceivedVoucherStatus.PENDING_OM_APPROVAL) {

            throw new IllegalStateException(
                    "Only a GRV awaiting Operations Manager approval can be rejected."
            );
        }

        requireName(operationsManager, "Operations Manager");
        requireName(reason, "Rejection reason");

        if (samePerson(operationsManager, voucher.getCreatedBy())
                || samePerson(operationsManager, voucher.getReceivedBy())
                || samePerson(operationsManager, voucher.getSubmittedBy())
                || samePerson(operationsManager, voucher.getVerifiedBy())) {

            throw new IllegalArgumentException(
                    "The Operations Manager rejecting this GRV must be independent "
                            + "of the GRV preparation and verification process."
            );
        }

        voucher.setRejectedBy(operationsManager.trim());
        voucher.setRejectionReason(reason.trim());
        voucher.setRejectionDate(LocalDateTime.now());

        voucher.setStatus(GoodsReceivedVoucherStatus.REJECTED);

        return goodsReceivedVoucherRepository.save(voucher);
    }


    // ---------------------------------------------------------
    // UPDATE PURCHASE ORDER RECEIVING STATUS
    // ---------------------------------------------------------

    private void updatePurchaseOrderStatus(PurchaseOrder purchaseOrder) {

        List<PurchaseOrderLine> orderLines =
                purchaseOrderLineRepository.findByPurchaseOrder_Id(
                        purchaseOrder.getId()
                );

        boolean allLinesFullyReceived = true;
        boolean anyQuantityReceived = false;

        for (PurchaseOrderLine orderLine : orderLines) {

            BigDecimal acceptedQuantity =
                    getPreviouslyAcceptedQuantity(orderLine.getId());

            if (acceptedQuantity.compareTo(BigDecimal.ZERO) > 0) {
                anyQuantityReceived = true;
            }

            if (acceptedQuantity.compareTo(orderLine.getQuantity()) < 0) {
                allLinesFullyReceived = false;
            }
        }

        if (allLinesFullyReceived && !orderLines.isEmpty()) {

            purchaseOrder.setStatus(
                    PurchaseOrderStatus.FULLY_RECEIVED
            );

        } else if (anyQuantityReceived) {

            purchaseOrder.setStatus(
                    PurchaseOrderStatus.PARTIALLY_RECEIVED
            );

        } else {

            purchaseOrder.setStatus(
                    PurchaseOrderStatus.ISSUED
            );
        }

        purchaseOrderRepository.save(purchaseOrder);
    }


    // ---------------------------------------------------------
    // CALCULATE PREVIOUSLY ACCEPTED QUANTITY
    // ---------------------------------------------------------

    private BigDecimal getPreviouslyAcceptedQuantity(
            Long purchaseOrderLineId) {

        List<GoodsReceivedVoucherLine> previousLines =
                goodsReceivedVoucherLineRepository
                        .findByPurchaseOrderLine_Id(purchaseOrderLineId);

        BigDecimal totalAccepted = BigDecimal.ZERO;

        for (GoodsReceivedVoucherLine line : previousLines) {

            GoodsReceivedVoucher previousVoucher =
                    line.getGoodsReceivedVoucher();

            if (previousVoucher.getApprovalDate() != null
                    && (previousVoucher.getStatus()
                    == GoodsReceivedVoucherStatus.PARTIALLY_ACCEPTED
                    || previousVoucher.getStatus()
                    == GoodsReceivedVoucherStatus.FULLY_ACCEPTED
                    || previousVoucher.getStatus()
                    == GoodsReceivedVoucherStatus.APPROVED)) {

                totalAccepted = totalAccepted.add(
                        line.getQuantityAccepted()
                );
            }
        }

        return totalAccepted;
    }


    // ---------------------------------------------------------
    // VALIDATE DELIVERY QUANTITIES
    // ---------------------------------------------------------

    private void validateDeliveryQuantities(
            BigDecimal delivered,
            BigDecimal accepted,
            BigDecimal rejected,
            BigDecimal damaged) {

        if (delivered == null || accepted == null
                || rejected == null || damaged == null) {

            throw new IllegalArgumentException(
                    "Delivery quantities cannot be null."
            );
        }

        if (delivered.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Delivered quantity must be greater than zero."
            );
        }

        if (accepted.compareTo(BigDecimal.ZERO) < 0
                || rejected.compareTo(BigDecimal.ZERO) < 0
                || damaged.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Accepted, rejected and damaged quantities cannot be negative."
            );
        }

        if (accepted.add(rejected).compareTo(delivered) != 0) {
            throw new IllegalArgumentException(
                    "Accepted quantity plus rejected quantity must equal delivered quantity."
            );
        }

        if (damaged.compareTo(rejected) > 0) {
            throw new IllegalArgumentException(
                    "Damaged quantity cannot exceed rejected quantity."
            );
        }
    }


    // ---------------------------------------------------------
    // FIND GRV OR THROW ERROR
    // ---------------------------------------------------------

    private GoodsReceivedVoucher getVoucher(Long id) {

        return goodsReceivedVoucherRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Goods Received Voucher not found: " + id
                        )
                );
    }


    // ---------------------------------------------------------
    // GENERATE GRV NUMBER
    // ---------------------------------------------------------

    private String generateGrvNumber() {

        int year = Year.now().getValue();

        long nextNumber = goodsReceivedVoucherRepository.count() + 1;

        return String.format(
                "BVC-GRV-%d-%05d",
                year,
                nextNumber
        );
    }


    // ---------------------------------------------------------
    // VALIDATE REQUIRED TEXT
    // ---------------------------------------------------------

    private void requireName(String value, String fieldName) {

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }
    }


    // ---------------------------------------------------------
    // COMPARE PERSON NAMES
    // ---------------------------------------------------------

    private boolean samePerson(String first, String second) {

        if (first == null || second == null) {
            return false;
        }

        return first.trim().equalsIgnoreCase(second.trim());
    }
}