package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.dto.StoresReviewLineRequest;
import com.replica.bellavistaerp.procurement.entity.ItemRequisition;

import java.util.List;

public interface ItemRequisitionService {

    // Requisition management
    ItemRequisition create(ItemRequisition requisition);

    List<ItemRequisition> findAll();

    ItemRequisition findById(Long id);

    // Approval workflow
    ItemRequisition submitForHeadApproval(
            Long requisitionId,
            String stockController
    );

    ItemRequisition approveByHead(
            Long requisitionId,
            String headName,
            String comments
    );

    ItemRequisition rejectByHead(
            Long requisitionId,
            String headName,
            String comments
    );

    ItemRequisition approveByOperationsManager(
            Long requisitionId,
            String operationsManager,
            String comments
    );

    ItemRequisition rejectByOperationsManager(
            Long requisitionId,
            String operationsManager,
            String comments
    );

    ItemRequisition reviewByStores(
        Long requisitionId,
        String stockController,
        List<StoresReviewLineRequest> reviewLines
    );
}