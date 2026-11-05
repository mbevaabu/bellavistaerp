package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.dto.PurchaseOrderCreateRequest;
import com.replica.bellavistaerp.procurement.entity.PurchaseOrder;
import com.replica.bellavistaerp.procurement.enums.PurchaseOrderStatus;
import com.replica.bellavistaerp.procurement.service.PurchaseOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement/purchase-orders")
@RequiredArgsConstructor
@Tag(
        name = "Purchase Orders",
        description = "Purchase Order creation, retrieval, issuance and cancellation"
)
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    // ---------------------------------------------------------
    // CREATE PURCHASE ORDER
    // ---------------------------------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create Purchase Order",
            description = "Creates a draft Purchase Order from an approved Purchase Request."
    )
    public PurchaseOrder create(
            @Valid @RequestBody PurchaseOrderCreateRequest request) {

        return purchaseOrderService.create(request);
    }

    // ---------------------------------------------------------
    // RETRIEVE PURCHASE ORDERS
    // ---------------------------------------------------------

    @GetMapping
    @Operation(
            summary = "Get all Purchase Orders",
            description = "Retrieves all Purchase Orders."
    )
    public List<PurchaseOrder> findAll() {

        return purchaseOrderService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Purchase Order by ID",
            description = "Retrieves a Purchase Order using its database ID."
    )
    public PurchaseOrder findById(@PathVariable Long id) {

        return purchaseOrderService.findById(id);
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(
            summary = "Get Purchase Order by number",
            description = "Retrieves a Purchase Order using its order number."
    )
    public PurchaseOrder findByOrderNumber(
            @PathVariable String orderNumber) {

        return purchaseOrderService.findByOrderNumber(orderNumber);
    }

    @GetMapping("/purchase-request/{purchaseRequestId}")
    @Operation(
            summary = "Get Purchase Orders by Purchase Request",
            description = "Retrieves all Purchase Orders generated from a specific Purchase Request."
    )
    public List<PurchaseOrder> findByPurchaseRequest(
            @PathVariable Long purchaseRequestId) {

        return purchaseOrderService.findByPurchaseRequest(
                purchaseRequestId
        );
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Get Purchase Orders by status",
            description = "Filters Purchase Orders by their current status."
    )
    public List<PurchaseOrder> findByStatus(
            @PathVariable PurchaseOrderStatus status) {

        return purchaseOrderService.findByStatus(status);
    }

    // ---------------------------------------------------------
    // ISSUE PURCHASE ORDER
    // ---------------------------------------------------------

    @PutMapping("/{id}/issue")
    @Operation(
            summary = "Issue Purchase Order",
            description = "Issues a draft Purchase Order to its supplier."
    )
    public PurchaseOrder issuePurchaseOrder(
            @PathVariable Long id,
            @RequestParam String issuedBy) {

        return purchaseOrderService.issuePurchaseOrder(
                id,
                issuedBy
        );
    }

    // ---------------------------------------------------------
    // CANCEL PURCHASE ORDER
    // ---------------------------------------------------------

    @PutMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel Purchase Order",
            description = "Cancels a draft Purchase Order with a recorded reason."
    )
    public PurchaseOrder cancelPurchaseOrder(
            @PathVariable Long id,
            @RequestParam String cancelledBy,
            @RequestParam String reason) {

        return purchaseOrderService.cancelPurchaseOrder(
                id,
                cancelledBy,
                reason
        );
    }
}