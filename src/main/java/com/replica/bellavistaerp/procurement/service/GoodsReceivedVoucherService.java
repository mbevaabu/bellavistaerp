package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.dto.GoodsReceivedVoucherCreateRequest;
import com.replica.bellavistaerp.procurement.entity.GoodsReceivedVoucher;
import com.replica.bellavistaerp.procurement.enums.GoodsReceivedVoucherStatus;

import java.util.List;

public interface GoodsReceivedVoucherService {

    // ---------------------------------------------------------
    // CREATE GRV
    // ---------------------------------------------------------

    GoodsReceivedVoucher create(
            GoodsReceivedVoucherCreateRequest request
    );


    // ---------------------------------------------------------
    // RETRIEVE GRVs
    // ---------------------------------------------------------

    List<GoodsReceivedVoucher> findAll();

    GoodsReceivedVoucher findById(Long id);

    GoodsReceivedVoucher findByGrvNumber(String grvNumber);

    List<GoodsReceivedVoucher> findByPurchaseOrder(Long purchaseOrderId);

    List<GoodsReceivedVoucher> findByStatus(
            GoodsReceivedVoucherStatus status
    );


    // ---------------------------------------------------------
    // SUBMIT FOR VERIFICATION
    // ---------------------------------------------------------

    GoodsReceivedVoucher submitForVerification(
            Long grvId,
            String submittedBy
    );


    // ---------------------------------------------------------
    // STOCK CONTROLLER VERIFICATION
    // ---------------------------------------------------------

    GoodsReceivedVoucher verifyByStockController(
            Long grvId,
            String stockController,
            String remarks
    );


    // ---------------------------------------------------------
    // OPERATIONS MANAGER APPROVAL
    // ---------------------------------------------------------

    GoodsReceivedVoucher approveByOperationsManager(
            Long grvId,
            String operationsManager,
            String comments
    );


    // ---------------------------------------------------------
    // OPERATIONS MANAGER REJECTION
    // ---------------------------------------------------------

    GoodsReceivedVoucher rejectByOperationsManager(
            Long grvId,
            String operationsManager,
            String reason
    );
}