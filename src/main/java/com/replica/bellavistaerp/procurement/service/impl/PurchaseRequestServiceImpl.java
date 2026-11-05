package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.dto.PurchaseRequestCreateRequest;
import com.replica.bellavistaerp.procurement.dto.PurchaseRequestLineRequest;
import com.replica.bellavistaerp.procurement.entity.Item;
import com.replica.bellavistaerp.procurement.entity.ItemRequisition;
import com.replica.bellavistaerp.procurement.entity.ItemRequisitionLine;
import com.replica.bellavistaerp.procurement.entity.PurchaseRequest;
import com.replica.bellavistaerp.procurement.entity.PurchaseRequestLine;
import com.replica.bellavistaerp.procurement.enums.PurchaseRequestStatus;
import com.replica.bellavistaerp.procurement.repository.ItemRepository;
import com.replica.bellavistaerp.procurement.repository.ItemRequisitionRepository;
import com.replica.bellavistaerp.procurement.repository.PurchaseRequestRepository;
import com.replica.bellavistaerp.procurement.service.PurchaseRequestService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private static final BigDecimal RA_APPROVAL_THRESHOLD =
            new BigDecimal("1000.00");

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ItemRequisitionRepository itemRequisitionRepository;
    private final ItemRepository itemRepository;

    // ---------------------------------------------------------
    // CREATE PURCHASE REQUEST
    // ---------------------------------------------------------

    @Override
    public PurchaseRequest create(PurchaseRequestCreateRequest request) {

        if (request == null || request.getRequisitionId() == null) {
            throw new IllegalArgumentException("Requisition ID is required.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one purchase request line is required."
            );
        }

        ItemRequisition requisition = itemRequisitionRepository
                .findById(request.getRequisitionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Item requisition not found."
                ));

        if (purchaseRequestRepository.existsByRequisition_Id(requisition.getId())) {
            throw new IllegalStateException(
                    "A purchase request already exists for this requisition."
            );
        }

        // Calculate shortages from Stores' verified recommendations.
        Map<Long, BigDecimal> shortages = new HashMap<>();
        Map<Long, String> unitsOfMeasure = new HashMap<>();

        for (ItemRequisitionLine requisitionLine : requisition.getItems()) {

            if (!requisitionLine.isStoresVerified()
                    || requisitionLine.getQuantityRecommended() == null) {
                throw new IllegalStateException(
                        "Stores must verify every requisition line before procurement."
                );
            }

            int requested = requisitionLine.getQuantity();
            int recommended = requisitionLine.getQuantityRecommended();

            if (recommended < 0 || recommended > requested) {
                throw new IllegalStateException(
                        "Invalid Stores recommendation for requisition line "
                                + requisitionLine.getId()
                );
            }

            int shortage = requested - recommended;

            if (shortage > 0) {

                Long itemId = requisitionLine.getItem().getId();

                shortages.merge(
                        itemId,
                        BigDecimal.valueOf(shortage),
                        BigDecimal::add
                );

                unitsOfMeasure.putIfAbsent(
                        itemId,
                        requisitionLine.getUnitOfMeasure()
                );
            }
        }

        PurchaseRequest purchaseRequest = new PurchaseRequest();

        purchaseRequest.setRequisition(requisition);
        purchaseRequest.setRequestedBy(request.getRequestedBy());
        purchaseRequest.setRequestingDepartment(
                requisition.getRequestingDepartment()
        );
        purchaseRequest.setJustification(request.getJustification());
        purchaseRequest.setStatus(PurchaseRequestStatus.DRAFT);

        BigDecimal estimatedTotal = BigDecimal.ZERO;
        Set<Long> processedItems = new HashSet<>();

        for (PurchaseRequestLineRequest lineRequest : request.getItems()) {

            if (lineRequest.getItemId() == null
                    || lineRequest.getQuantity() == null
                    || lineRequest.getEstimatedUnitPrice() == null) {
                throw new IllegalArgumentException(
                        "Item, quantity and estimated unit price are required."
                );
            }

            if (lineRequest.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "Purchase quantity must be greater than zero."
                );
            }

            if (lineRequest.getEstimatedUnitPrice()
                    .compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Estimated unit price cannot be negative."
                );
            }

            Long itemId = lineRequest.getItemId();

            if (!processedItems.add(itemId)) {
                throw new IllegalArgumentException(
                        "An item may only appear once in a purchase request."
                );
            }

            BigDecimal shortage = shortages.get(itemId);

            if (shortage == null || shortage.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "The selected item has no verified shortage in the requisition."
                );
            }

            if (lineRequest.getQuantity().compareTo(shortage) > 0) {
                throw new IllegalArgumentException(
                        "Purchase quantity exceeds the verified shortage for item ID "
                                + itemId
                );
            }

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Item not found: " + itemId
                    ));

            PurchaseRequestLine purchaseLine = new PurchaseRequestLine();

            purchaseLine.setItem(item);
            purchaseLine.setQuantity(lineRequest.getQuantity());
            purchaseLine.setUnitOfMeasure(unitsOfMeasure.get(itemId));
            purchaseLine.setEstimatedUnitPrice(
                    lineRequest.getEstimatedUnitPrice()
            );
            purchaseLine.setRemarks(lineRequest.getRemarks());

            BigDecimal lineTotal = lineRequest.getQuantity()
                    .multiply(lineRequest.getEstimatedUnitPrice());

            purchaseLine.setEstimatedLineTotal(lineTotal);

            purchaseRequest.addItem(purchaseLine);

            estimatedTotal = estimatedTotal.add(lineTotal);
        }

        purchaseRequest.setEstimatedTotal(estimatedTotal);

        String requestNumber = "BVC-PR-"
                + Year.now().getValue()
                + "-"
                + String.format(
                        "%05d",
                        purchaseRequestRepository.count() + 1
                );

        purchaseRequest.setRequestNumber(requestNumber);

        return purchaseRequestRepository.save(purchaseRequest);
    }

    // ---------------------------------------------------------
    // RETRIEVE PURCHASE REQUESTS
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseRequest> findAll() {
        return purchaseRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequest findById(Long id) {
        return getRequest(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequest findByRequestNumber(String requestNumber) {

        return purchaseRequestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Purchase request not found: " + requestNumber
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequest findByRequisition(Long requisitionId) {

        return purchaseRequestRepository.findByRequisition_Id(requisitionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No purchase request exists for this requisition."
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseRequest> findByStatus(PurchaseRequestStatus status) {
        return purchaseRequestRepository.findByStatus(status);
    }

    // ---------------------------------------------------------
    // OPERATIONS MANAGER SUBMISSION
    // ---------------------------------------------------------

    @Override
    public PurchaseRequest submitForDirectorateApproval(
            Long purchaseRequestId,
            String operationsManager,
            String comments) {

        PurchaseRequest request = getRequest(purchaseRequestId);

        if (request.getStatus() != PurchaseRequestStatus.DRAFT
                && request.getStatus() != PurchaseRequestStatus.RETURNED_TO_OM) {
            throw new IllegalStateException(
                    "Purchase request is not awaiting OM submission."
            );
        }

        requireName(operationsManager, "Operations Manager");

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalStateException(
                    "Purchase request must contain at least one item."
            );
        }

        if (request.getEstimatedTotal() == null
                || request.getEstimatedTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "Purchase request total must be greater than zero."
            );
        }

        request.setOperationsManager(operationsManager);
        request.setOmComments(comments);
        request.setOmReviewDate(LocalDateTime.now());
        request.setStatus(
                PurchaseRequestStatus.PENDING_DIRECTORATE_APPROVAL
        );

        return purchaseRequestRepository.save(request);
    }

    // ---------------------------------------------------------
    // OD1 APPROVAL AND REJECTION
    // ---------------------------------------------------------

    @Override
    public PurchaseRequest approveByOD1(
            Long purchaseRequestId,
            String approver,
            String comments) {

        PurchaseRequest request = getRequest(purchaseRequestId);

        validateDirectorateApproval(request);
        requireName(approver, "OD1 approver");

        if (request.getOd1ApprovedBy() != null) {
            throw new IllegalStateException(
                    "OD1 has already approved this purchase request."
            );
        }

        request.setOd1ApprovedBy(approver);
        request.setOd1Comments(comments);
        request.setOd1ApprovalDate(LocalDateTime.now());

        updateApprovalStatus(request);

        return purchaseRequestRepository.save(request);
    }

    @Override
    public PurchaseRequest rejectByOD1(
            Long purchaseRequestId,
            String rejector,
            String comments) {

        PurchaseRequest request = getRequest(purchaseRequestId);

        validateDirectorateApproval(request);
        requireName(rejector, "OD1 rejector");
        requireComments(comments);

        request.setOd1RejectedBy(rejector);
        request.setOd1RejectionComments(comments);
        request.setOd1RejectionDate(LocalDateTime.now());

        clearPreviousApprovals(request);

        request.setStatus(PurchaseRequestStatus.RETURNED_TO_OM);

        return purchaseRequestRepository.save(request);
    }

    // ---------------------------------------------------------
    // OD2 APPROVAL AND REJECTION
    // ---------------------------------------------------------

    @Override
    public PurchaseRequest approveByOD2(
            Long purchaseRequestId,
            String approver,
            String comments) {

        PurchaseRequest request = getRequest(purchaseRequestId);

        validateDirectorateApproval(request);
        requireName(approver, "OD2 approver");

        if (request.getOd2ApprovedBy() != null) {
            throw new IllegalStateException(
                    "OD2 has already approved this purchase request."
            );
        }

        request.setOd2ApprovedBy(approver);
        request.setOd2Comments(comments);
        request.setOd2ApprovalDate(LocalDateTime.now());

        updateApprovalStatus(request);

        return purchaseRequestRepository.save(request);
    }

    @Override
    public PurchaseRequest rejectByOD2(
            Long purchaseRequestId,
            String rejector,
            String comments) {

        PurchaseRequest request = getRequest(purchaseRequestId);

        validateDirectorateApproval(request);
        requireName(rejector, "OD2 rejector");
        requireComments(comments);

        request.setOd2RejectedBy(rejector);
        request.setOd2RejectionComments(comments);
        request.setOd2RejectionDate(LocalDateTime.now());

        clearPreviousApprovals(request);

        request.setStatus(PurchaseRequestStatus.RETURNED_TO_OM);

        return purchaseRequestRepository.save(request);
    }

    // ---------------------------------------------------------
    // RESPONSIBLE AUTHORITY APPROVAL AND REJECTION
    // ---------------------------------------------------------

    @Override
    public PurchaseRequest approveByRA(
            Long purchaseRequestId,
            String approver,
            String comments) {

        PurchaseRequest request = getRequest(purchaseRequestId);

        if (request.getStatus() != PurchaseRequestStatus.PENDING_RA_APPROVAL) {
            throw new IllegalStateException(
                    "Purchase request is not awaiting RA approval."
            );
        }

        requireName(approver, "Responsible Authority");

        request.setResponsibleAuthority(approver);
        request.setRaComments(comments);
        request.setRaApprovalDate(LocalDateTime.now());
        request.setStatus(PurchaseRequestStatus.APPROVED);

        return purchaseRequestRepository.save(request);
    }

    @Override
    public PurchaseRequest rejectByRA(
            Long purchaseRequestId,
            String rejector,
            String comments) {

        PurchaseRequest request = getRequest(purchaseRequestId);

        if (request.getStatus() != PurchaseRequestStatus.PENDING_RA_APPROVAL) {
            throw new IllegalStateException(
                    "Purchase request is not awaiting RA approval."
            );
        }

        requireName(rejector, "Responsible Authority");
        requireComments(comments);

        request.setRaRejectedBy(rejector);
        request.setRaRejectionComments(comments);
        request.setRaRejectionDate(LocalDateTime.now());

        clearPreviousApprovals(request);

        request.setStatus(PurchaseRequestStatus.RETURNED_TO_OM);

        return purchaseRequestRepository.save(request);
    }

    // ---------------------------------------------------------
    // APPROVAL STATUS MANAGEMENT
    // ---------------------------------------------------------

    private void updateApprovalStatus(PurchaseRequest request) {

        boolean od1Approved = request.getOd1ApprovedBy() != null;
        boolean od2Approved = request.getOd2ApprovedBy() != null;

        if (od1Approved && od2Approved) {

            if (request.getEstimatedTotal()
                    .compareTo(RA_APPROVAL_THRESHOLD) >= 0) {

                request.setStatus(
                        PurchaseRequestStatus.PENDING_RA_APPROVAL
                );

            } else {

                request.setStatus(PurchaseRequestStatus.APPROVED);
            }
        }
    }

    private void clearPreviousApprovals(PurchaseRequest request) {

        request.setOd1ApprovedBy(null);
        request.setOd1Comments(null);
        request.setOd1ApprovalDate(null);

        request.setOd2ApprovedBy(null);
        request.setOd2Comments(null);
        request.setOd2ApprovalDate(null);

        request.setResponsibleAuthority(null);
        request.setRaComments(null);
        request.setRaApprovalDate(null);
    }

    // ---------------------------------------------------------
    // VALIDATION AND HELPER METHODS
    // ---------------------------------------------------------

    private void validateDirectorateApproval(PurchaseRequest request) {

        if (request.getStatus()
                != PurchaseRequestStatus.PENDING_DIRECTORATE_APPROVAL) {

            throw new IllegalStateException(
                    "Purchase request is not awaiting directorate approval."
            );
        }
    }

    private void requireName(String name, String fieldName) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " name is required."
            );
        }
    }

    private void requireComments(String comments) {

        if (comments == null || comments.isBlank()) {
            throw new IllegalArgumentException(
                    "Comments are required when rejecting a purchase request."
            );
        }
    }

    private PurchaseRequest getRequest(Long id) {

        return purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Purchase request not found with ID: " + id
                ));
    }
}