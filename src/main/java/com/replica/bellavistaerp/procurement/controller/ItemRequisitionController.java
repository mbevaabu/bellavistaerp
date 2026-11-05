
package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.dto.StoresReviewLineRequest;
import com.replica.bellavistaerp.procurement.entity.ItemRequisition;
import com.replica.bellavistaerp.procurement.service.ItemRequisitionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement/requisitions")
@RequiredArgsConstructor
@Tag(
        name = "Item Requisitions",
        description = "Manage requisitions, Stores reviews and approval workflow"
)
public class ItemRequisitionController {

    private final ItemRequisitionService requisitionService;

    // CREATE REQUISITION
    @PostMapping
    @Operation(summary = "Create a new requisition")
    public ItemRequisition create(
            @RequestBody ItemRequisition requisition) {

        return requisitionService.create(requisition);
    }

    // GET ALL REQUISITIONS
    @GetMapping
    @Operation(summary = "Get all requisitions")
    public List<ItemRequisition> findAll() {

        return requisitionService.findAll();
    }

    // GET REQUISITION BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Get a requisition by ID")
    public ItemRequisition findById(
            @PathVariable Long id) {

        return requisitionService.findById(id);
    }

    // STORES REVIEW
    @PostMapping("/{id}/stores-review")
    @Operation(summary = "Review requisition items and record stock availability")
    public ItemRequisition reviewByStores(
            @PathVariable Long id,
            @RequestParam String stockController,
            @RequestBody List<StoresReviewLineRequest> reviewLines) {

        return requisitionService.reviewByStores(
                id,
                stockController,
                reviewLines
        );
    }

    // SUBMIT FOR HEAD APPROVAL
    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit a Stores-reviewed requisition for Head approval")
    public ItemRequisition submitForHeadApproval(
            @PathVariable Long id,
            @RequestParam String stockController) {

        return requisitionService.submitForHeadApproval(
                id,
                stockController
        );
    }

    // HEAD APPROVAL
    @PostMapping("/{id}/head/approve")
    @Operation(summary = "Approve a requisition as Head")
    public ItemRequisition approveByHead(
            @PathVariable Long id,
            @RequestParam String headName,
            @RequestParam(required = false) String comments) {

        return requisitionService.approveByHead(
                id,
                headName,
                comments
        );
    }

    // HEAD REJECTION
    @PostMapping("/{id}/head/reject")
    @Operation(summary = "Reject a requisition as Head")
    public ItemRequisition rejectByHead(
            @PathVariable Long id,
            @RequestParam String headName,
            @RequestParam String comments) {

        return requisitionService.rejectByHead(
                id,
                headName,
                comments
        );
    }

    // OPERATIONS MANAGER APPROVAL
    @PostMapping("/{id}/operations-manager/approve")
    @Operation(summary = "Approve a requisition as Operations Manager")
    public ItemRequisition approveByOperationsManager(
            @PathVariable Long id,
            @RequestParam String operationsManager,
            @RequestParam(required = false) String comments) {

        return requisitionService.approveByOperationsManager(
                id,
                operationsManager,
                comments
        );
    }

    // OPERATIONS MANAGER REJECTION
    @PostMapping("/{id}/operations-manager/reject")
    @Operation(summary = "Reject a requisition as Operations Manager")
    public ItemRequisition rejectByOperationsManager(
            @PathVariable Long id,
            @RequestParam String operationsManager,
            @RequestParam String comments) {

        return requisitionService.rejectByOperationsManager(
                id,
                operationsManager,
                comments
        );
    }
}
