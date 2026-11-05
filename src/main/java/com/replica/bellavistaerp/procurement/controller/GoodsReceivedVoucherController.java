package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.dto.GoodsReceivedVoucherCreateRequest;
import com.replica.bellavistaerp.procurement.entity.GoodsReceivedVoucher;
import com.replica.bellavistaerp.procurement.enums.GoodsReceivedVoucherStatus;
import com.replica.bellavistaerp.procurement.service.GoodsReceivedVoucherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement/grvs")
@RequiredArgsConstructor
@Tag(
    name = "Goods Received Vouchers",
    description = "Manage supplier deliveries, goods verification and GRV approvals"
)
public class GoodsReceivedVoucherController {

    private final GoodsReceivedVoucherService goodsReceivedVoucherService;


    // ---------------------------------------------------------
    // CREATE GRV
    // ---------------------------------------------------------

    @PostMapping
    @Operation(
        summary = "Create a Goods Received Voucher",
        description = "Records a supplier delivery against an issued Purchase Order."
    )
    public ResponseEntity<GoodsReceivedVoucher> create(
            @Valid @RequestBody GoodsReceivedVoucherCreateRequest request) {

        GoodsReceivedVoucher voucher =
                goodsReceivedVoucherService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(voucher);
    }


    // ---------------------------------------------------------
    // GET ALL GRVs
    // ---------------------------------------------------------

    @GetMapping
    @Operation(
        summary = "Get all GRVs",
        description = "Retrieves all Goods Received Vouchers."
    )
    public ResponseEntity<List<GoodsReceivedVoucher>> findAll() {

        return ResponseEntity.ok(
                goodsReceivedVoucherService.findAll()
        );
    }


    // ---------------------------------------------------------
    // GET GRV BY ID
    // ---------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(
        summary = "Get GRV by ID",
        description = "Retrieves a Goods Received Voucher using its database ID."
    )
    public ResponseEntity<GoodsReceivedVoucher> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                goodsReceivedVoucherService.findById(id)
        );
    }


    // ---------------------------------------------------------
    // GET GRV BY NUMBER
    // ---------------------------------------------------------

    @GetMapping("/number/{grvNumber}")
    @Operation(
        summary = "Get GRV by number",
        description = "Retrieves a Goods Received Voucher using its unique GRV number."
    )
    public ResponseEntity<GoodsReceivedVoucher> findByGrvNumber(
            @PathVariable String grvNumber) {

        return ResponseEntity.ok(
                goodsReceivedVoucherService.findByGrvNumber(grvNumber)
        );
    }


    // ---------------------------------------------------------
    // GET GRVs BY PURCHASE ORDER
    // ---------------------------------------------------------

    @GetMapping("/purchase-order/{purchaseOrderId}")
    @Operation(
        summary = "Get GRVs by Purchase Order",
        description = "Retrieves all delivery records associated with a Purchase Order."
    )
    public ResponseEntity<List<GoodsReceivedVoucher>> findByPurchaseOrder(
            @PathVariable Long purchaseOrderId) {

        return ResponseEntity.ok(
                goodsReceivedVoucherService.findByPurchaseOrder(
                        purchaseOrderId
                )
        );
    }


    // ---------------------------------------------------------
    // GET GRVs BY STATUS
    // ---------------------------------------------------------

    @GetMapping("/status/{status}")
    @Operation(
        summary = "Get GRVs by status",
        description = "Filters Goods Received Vouchers by their workflow status."
    )
    public ResponseEntity<List<GoodsReceivedVoucher>> findByStatus(
            @PathVariable GoodsReceivedVoucherStatus status) {

        return ResponseEntity.ok(
                goodsReceivedVoucherService.findByStatus(status)
        );
    }


    // ---------------------------------------------------------
    // SUBMIT FOR VERIFICATION
    // ---------------------------------------------------------

    @PutMapping("/{id}/submit")
    @Operation(
        summary = "Submit GRV for verification",
        description = "Moves a draft GRV to pending Stock Controller verification."
    )
    public ResponseEntity<GoodsReceivedVoucher> submitForVerification(
            @PathVariable Long id,
            @RequestParam String submittedBy) {

        return ResponseEntity.ok(
                goodsReceivedVoucherService.submitForVerification(
                        id,
                        submittedBy
                )
        );
    }


    // ---------------------------------------------------------
    // STOCK CONTROLLER VERIFICATION
    // ---------------------------------------------------------

    @PutMapping("/{id}/verify")
    @Operation(
        summary = "Verify GRV",
        description = "Records the Stock Controller's verification and sends the GRV to the Operations Manager."
    )
    public ResponseEntity<GoodsReceivedVoucher> verifyByStockController(
            @PathVariable Long id,
            @RequestParam String stockController,
            @RequestParam(required = false) String remarks) {

        return ResponseEntity.ok(
                goodsReceivedVoucherService.verifyByStockController(
                        id,
                        stockController,
                        remarks
                )
        );
    }


    // ---------------------------------------------------------
    // OPERATIONS MANAGER APPROVAL
    // ---------------------------------------------------------

    @PutMapping("/{id}/approve")
    @Operation(
        summary = "Approve GRV",
        description = "The Operations Manager approves the GRV. Accepted quantities are then posted into stock."
    )
    public ResponseEntity<GoodsReceivedVoucher> approveByOperationsManager(
            @PathVariable Long id,
            @RequestParam String operationsManager,
            @RequestParam(required = false) String comments) {

        return ResponseEntity.ok(
                goodsReceivedVoucherService.approveByOperationsManager(
                        id,
                        operationsManager,
                        comments
                )
        );
    }


    // ---------------------------------------------------------
    // OPERATIONS MANAGER REJECTION
    // ---------------------------------------------------------

    @PutMapping("/{id}/reject")
    @Operation(
        summary = "Reject GRV",
        description = "The Operations Manager rejects the GRV and records the reason. No stock is posted."
    )
    public ResponseEntity<GoodsReceivedVoucher> rejectByOperationsManager(
            @PathVariable Long id,
            @RequestParam String operationsManager,
            @RequestParam String reason) {

        return ResponseEntity.ok(
                goodsReceivedVoucherService.rejectByOperationsManager(
                        id,
                        operationsManager,
                        reason
                )
        );
    }
}