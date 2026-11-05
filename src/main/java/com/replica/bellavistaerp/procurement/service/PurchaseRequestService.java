package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.dto.PurchaseRequestCreateRequest;
import com.replica.bellavistaerp.procurement.entity.PurchaseRequest;
import com.replica.bellavistaerp.procurement.enums.PurchaseRequestStatus;

import java.util.List;

public interface PurchaseRequestService {

    // Create purchase request
    PurchaseRequest create(PurchaseRequestCreateRequest request);

    // Retrieve purchase requests
    List<PurchaseRequest> findAll();

    PurchaseRequest findById(Long id);

    PurchaseRequest findByRequestNumber(String requestNumber);

    PurchaseRequest findByRequisition(Long requisitionId);

    List<PurchaseRequest> findByStatus(PurchaseRequestStatus status);

    // Operations Manager submission
    PurchaseRequest submitForDirectorateApproval(
            Long purchaseRequestId,
            String operationsManager,
            String comments
    );

    // OD1 approval and rejection
    PurchaseRequest approveByOD1(
            Long purchaseRequestId,
            String approver,
            String comments
    );

    PurchaseRequest rejectByOD1(
            Long purchaseRequestId,
            String rejector,
            String comments
    );

    // OD2 approval and rejection
    PurchaseRequest approveByOD2(
            Long purchaseRequestId,
            String approver,
            String comments
    );

    PurchaseRequest rejectByOD2(
            Long purchaseRequestId,
            String rejector,
            String comments
    );

    // Responsible Authority approval and rejection
    PurchaseRequest approveByRA(
            Long purchaseRequestId,
            String approver,
            String comments
    );

    PurchaseRequest rejectByRA(
            Long purchaseRequestId,
            String rejector,
            String comments
    );
}