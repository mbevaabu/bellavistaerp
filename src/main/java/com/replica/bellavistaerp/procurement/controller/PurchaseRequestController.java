package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.dto.PurchaseRequestCreateRequest;
import com.replica.bellavistaerp.procurement.entity.PurchaseRequest;
import com.replica.bellavistaerp.procurement.enums.PurchaseRequestStatus;
import com.replica.bellavistaerp.procurement.service.PurchaseRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement/purchase-requests")
@RequiredArgsConstructor
@Tag(
        name = "Purchase Requests",
        description = "Purchase request management and approval workflow"
)
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    // ---------------------------------------------------------
    // CREATE PURCHASE REQUEST
    // ---------------------------------------------------------

    @PostMapping
    @Operation(summary = "Create a purchase request from an item requisition")
    public PurchaseRequest create(
            @Valid @RequestBody PurchaseRequestCreateRequest request) {

        return purchaseRequestService.create(request);
    }

    // ---------------------------------------------------------
    // RETRIEVE PURCHASE REQUESTS
    // ---------------------------------------------------------

    @GetMapping
    @Operation(summary = "Get all purchase requests")
    public List<PurchaseRequest> findAll() {

        return purchaseRequestService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a purchase request by ID")
    public PurchaseRequest findById(@PathVariable Long id) {

        return purchaseRequestService.findById(id);
    }

    @GetMapping("/number/{requestNumber}")
    @Operation(summary = "Get a purchase request by request number")
    public PurchaseRequest findByRequestNumber(
            @PathVariable String requestNumber) {

        return purchaseRequestService.findByRequestNumber(requestNumber);
    }

    @GetMapping("/requisition/{requisitionId}")
    @Operation(summary = "Get a purchase request by its originating requisition")
    public PurchaseRequest findByRequisition(
            @PathVariable Long requisitionId) {

        return purchaseRequestService.findByRequisition(requisitionId);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get purchase requests by status")
    public List<PurchaseRequest> findByStatus(
            @PathVariable PurchaseRequestStatus status) {

        return purchaseRequestService.findByStatus(status);
    }

    // ---------------------------------------------------------
    // OPERATIONS MANAGER
    // ---------------------------------------------------------

    @PostMapping("/{id}/submit-for-directorate-approval")
    @Operation(summary = "OM submits a purchase request to OD1 and OD2")
    public PurchaseRequest submitForDirectorateApproval(
            @PathVariable Long id,
            @RequestParam String operationsManager,
            @RequestParam(required = false) String comments) {

        return purchaseRequestService.submitForDirectorateApproval(
                id,
                operationsManager,
                comments
        );
    }

    // ---------------------------------------------------------
    // OD1 APPROVAL AND REJECTION
    // ---------------------------------------------------------

    @PostMapping("/{id}/od1/approve")
    @Operation(summary = "OD1 approves a purchase request")
    public PurchaseRequest approveByOD1(
            @PathVariable Long id,
            @RequestParam String approver,
            @RequestParam(required = false) String comments) {

        return purchaseRequestService.approveByOD1(
                id,
                approver,
                comments
        );
    }

    @PostMapping("/{id}/od1/reject")
    @Operation(summary = "OD1 rejects a purchase request")
    public PurchaseRequest rejectByOD1(
            @PathVariable Long id,
            @RequestParam String rejector,
            @RequestParam String comments) {

        return purchaseRequestService.rejectByOD1(
                id,
                rejector,
                comments
        );
    }

    // ---------------------------------------------------------
    // OD2 APPROVAL AND REJECTION
    // ---------------------------------------------------------

    @PostMapping("/{id}/od2/approve")
    @Operation(summary = "OD2 approves a purchase request")
    public PurchaseRequest approveByOD2(
            @PathVariable Long id,
            @RequestParam String approver,
            @RequestParam(required = false) String comments) {

        return purchaseRequestService.approveByOD2(
                id,
                approver,
                comments
        );
    }

    @PostMapping("/{id}/od2/reject")
    @Operation(summary = "OD2 rejects a purchase request")
    public PurchaseRequest rejectByOD2(
            @PathVariable Long id,
            @RequestParam String rejector,
            @RequestParam String comments) {

        return purchaseRequestService.rejectByOD2(
                id,
                rejector,
                comments
        );
    }

    // ---------------------------------------------------------
    // RESPONSIBLE AUTHORITY
    // ---------------------------------------------------------

    @PostMapping("/{id}/ra/approve")
    @Operation(summary = "Responsible Authority approves a purchase request")
    public PurchaseRequest approveByRA(
            @PathVariable Long id,
            @RequestParam String approver,
            @RequestParam(required = false) String comments) {

        return purchaseRequestService.approveByRA(
                id,
                approver,
                comments
        );
    }

    @PostMapping("/{id}/ra/reject")
    @Operation(summary = "Responsible Authority rejects a purchase request")
    public PurchaseRequest rejectByRA(
            @PathVariable Long id,
            @RequestParam String rejector,
            @RequestParam String comments) {

        return purchaseRequestService.rejectByRA(
                id,
                rejector,
                comments
        );
    }
}