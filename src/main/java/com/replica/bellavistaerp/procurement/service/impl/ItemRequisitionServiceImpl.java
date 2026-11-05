
package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.dto.StoresReviewLineRequest;
import com.replica.bellavistaerp.procurement.entity.Item;
import com.replica.bellavistaerp.procurement.entity.ItemRequisition;
import com.replica.bellavistaerp.procurement.entity.ItemRequisitionLine;
import com.replica.bellavistaerp.procurement.enums.RequisitionStatus;
import com.replica.bellavistaerp.procurement.repository.ItemRepository;
import com.replica.bellavistaerp.procurement.repository.ItemRequisitionRepository;
import com.replica.bellavistaerp.procurement.service.ItemRequisitionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequisitionServiceImpl implements ItemRequisitionService {

    private final ItemRequisitionRepository repository;
    private final ItemRepository itemRepository;

    // =========================================================
    // CREATE REQUISITION
    // =========================================================

    @Override
    public ItemRequisition create(ItemRequisition requisition) {

        if (requisition == null) {
            throw new IllegalArgumentException(
                    "Requisition cannot be empty."
            );
        }

        if (requisition.getItems() == null
                || requisition.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "A requisition must contain at least one item."
            );
        }

        // Validate and resolve every inventory item.
        for (ItemRequisitionLine line : requisition.getItems()) {

            if (line == null || line.getItem() == null
                    || line.getItem().getId() == null) {
                throw new IllegalArgumentException(
                        "Every requisition line must reference a valid inventory item."
                );
            }

            if (line.getQuantity() == null || line.getQuantity() < 1) {
                throw new IllegalArgumentException(
                        "Requisition quantity must be at least 1."
                );
            }

            Long itemId = line.getItem().getId();

            Item inventoryItem = itemRepository.findById(itemId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Inventory item not found: " + itemId
                            )
                    );

            line.setItem(inventoryItem);
            line.setRequisition(requisition);
        }

        String prefix = "BVC-REQ-" + Year.now().getValue() + "-";

        String number = prefix + String.format(
                "%05d",
                repository.count() + 1
        );

        requisition.setRequisitionNumber(number);
        requisition.setStatus(RequisitionStatus.NEW);

        return repository.save(requisition);
    }

    // =========================================================
    // RETRIEVE REQUISITIONS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequisition> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequisition findById(Long id) {
        return getRequisition(id);
    }

    // =========================================================
    // STORES REVIEW
    // =========================================================

    @Override
    public ItemRequisition reviewByStores(
            Long requisitionId,
            String stockController,
            List<StoresReviewLineRequest> reviewLines) {

        ItemRequisition requisition = getRequisition(requisitionId);

        requireName(stockController, "Stock Controller");

        if (requisition.getStatus() != RequisitionStatus.NEW
                && requisition.getStatus() != RequisitionStatus.STORES_REVIEW) {
            throw new IllegalStateException(
                    "Only new or stores-reviewed requisitions can be reviewed."
            );
        }

        if (requisition.getItems() == null
                || requisition.getItems().isEmpty()) {
            throw new IllegalStateException(
                    "This requisition has no items to review."
            );
        }

        if (reviewLines == null
                || reviewLines.size() != requisition.getItems().size()) {
            throw new IllegalArgumentException(
                    "Every requisition item must be assessed."
            );
        }

        Set<Long> reviewedLineIds = new HashSet<>();

        for (StoresReviewLineRequest review : reviewLines) {

            if (review == null
                    || review.getRequisitionLineId() == null
                    || review.getQuantityAvailable() == null
                    || review.getQuantityRecommended() == null) {
                throw new IllegalArgumentException(
                        "All Stores assessment fields are required."
                );
            }

            Long lineId = review.getRequisitionLineId();

            if (!reviewedLineIds.add(lineId)) {
                throw new IllegalArgumentException(
                        "Duplicate requisition line assessment: " + lineId
                );
            }

            ItemRequisitionLine line = requisition.getItems()
                    .stream()
                    .filter(item -> lineId.equals(item.getId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Requisition line does not belong to this requisition: "
                                            + lineId
                            )
                    );

            Integer available = review.getQuantityAvailable();
            Integer recommended = review.getQuantityRecommended();

            if (available < 0 || recommended < 0) {
                throw new IllegalArgumentException(
                        "Stock quantities cannot be negative."
                );
            }

            if (recommended > available) {
                throw new IllegalArgumentException(
                        "Recommended quantity cannot exceed available stock."
                );
            }

            if (recommended > line.getQuantity()) {
                throw new IllegalArgumentException(
                        "Recommended quantity cannot exceed requested quantity."
                );
            }

            line.setQuantityAvailable(available);
            line.setQuantityRecommended(recommended);
            line.setQuantityShortage(line.getQuantity() - recommended);
            line.setStoresRemarks(review.getStoresRemarks());
            line.setStoresVerified(true);
        }

        requisition.setStockController(stockController);
        requisition.setStatus(RequisitionStatus.STORES_REVIEW);

        return repository.save(requisition);
    }

    // =========================================================
    // SUBMIT FOR HEAD APPROVAL
    // =========================================================

    @Override
    public ItemRequisition submitForHeadApproval(
            Long requisitionId,
            String stockController) {

        ItemRequisition requisition = getRequisition(requisitionId);

        if (requisition.getStatus() != RequisitionStatus.NEW
                && requisition.getStatus() != RequisitionStatus.STORES_REVIEW) {
            throw new IllegalStateException(
                    "Only new or stores-reviewed requisitions can be submitted."
            );
        }

        requireName(stockController, "Stock Controller");

        if (requisition.getItems() == null
                || requisition.getItems().isEmpty()) {
            throw new IllegalStateException(
                    "A requisition must contain at least one item."
            );
        }

        boolean allItemsReviewed = requisition.getItems()
                .stream()
                .allMatch(ItemRequisitionLine::isStoresVerified);

        if (!allItemsReviewed) {
            throw new IllegalStateException(
                    "Stores must review every requisition item before submission."
            );
        }

        requisition.setStockController(stockController);
        requisition.setSubmittedForApprovalAt(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.PENDING_HEAD_APPROVAL);

        return repository.save(requisition);
    }

    // =========================================================
    // HEAD APPROVAL
    // =========================================================

    @Override
    public ItemRequisition approveByHead(
            Long requisitionId,
            String headName,
            String comments) {

        ItemRequisition requisition = getRequisition(requisitionId);

        requireStatus(
                requisition,
                RequisitionStatus.PENDING_HEAD_APPROVAL
        );

        requireName(headName, "Head");

        requisition.setHeadApprovedBy(headName);
        requisition.setHeadApprovalComments(comments);
        requisition.setHeadApprovalDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.PENDING_OM_APPROVAL);

        return repository.save(requisition);
    }

    @Override
    public ItemRequisition rejectByHead(
            Long requisitionId,
            String headName,
            String comments) {

        ItemRequisition requisition = getRequisition(requisitionId);

        requireStatus(
                requisition,
                RequisitionStatus.PENDING_HEAD_APPROVAL
        );

        requireName(headName, "Head");
        requireComments(comments);

        requisition.setHeadApprovedBy(headName);
        requisition.setHeadApprovalComments(comments);
        requisition.setHeadApprovalDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.REJECTED);

        return repository.save(requisition);
    }

    // =========================================================
    // OPERATIONS MANAGER APPROVAL
    // =========================================================

    @Override
    public ItemRequisition approveByOperationsManager(
            Long requisitionId,
            String operationsManager,
            String comments) {

        ItemRequisition requisition = getRequisition(requisitionId);

        requireStatus(
                requisition,
                RequisitionStatus.PENDING_OM_APPROVAL
        );

        requireName(operationsManager, "Operations Manager");

        requisition.setOperationsManagerApprovedBy(operationsManager);
        requisition.setOperationsManagerComments(comments);
        requisition.setOperationsManagerApprovalDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.READY_FOR_ISSUE);

        return repository.save(requisition);
    }

    @Override
    public ItemRequisition rejectByOperationsManager(
            Long requisitionId,
            String operationsManager,
            String comments) {

        ItemRequisition requisition = getRequisition(requisitionId);

        requireStatus(
                requisition,
                RequisitionStatus.PENDING_OM_APPROVAL
        );

        requireName(operationsManager, "Operations Manager");
        requireComments(comments);

        requisition.setOperationsManagerApprovedBy(operationsManager);
        requisition.setOperationsManagerComments(comments);
        requisition.setOperationsManagerApprovalDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.REJECTED);

        return repository.save(requisition);
    }

    // =========================================================
    // VALIDATION HELPERS
    // =========================================================

    private ItemRequisition getRequisition(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Requisition ID is required."
            );
        }

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Requisition not found: " + id
                        )
                );
    }

    private void requireStatus(
            ItemRequisition requisition,
            RequisitionStatus expectedStatus) {

        if (requisition.getStatus() != expectedStatus) {
            throw new IllegalStateException(
                    "Invalid requisition status. Expected "
                            + expectedStatus
                            + " but found "
                            + requisition.getStatus()
            );
        }
    }

    private void requireName(String name, String role) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    role + " name is required."
            );
        }
    }

    private void requireComments(String comments) {

        if (comments == null || comments.isBlank()) {
            throw new IllegalArgumentException(
                    "Comments are mandatory when rejecting a requisition."
            );
        }
    }
}
