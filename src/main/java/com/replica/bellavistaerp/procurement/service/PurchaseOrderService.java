package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.dto.PurchaseOrderCreateRequest;
import com.replica.bellavistaerp.procurement.entity.PurchaseOrder;
import com.replica.bellavistaerp.procurement.enums.PurchaseOrderStatus;

import java.util.List;

public interface PurchaseOrderService {

    // Create Purchase Order
    PurchaseOrder create(
            PurchaseOrderCreateRequest request
    );

    // Retrieve Purchase Orders
    List<PurchaseOrder> findAll();

    PurchaseOrder findById(Long id);

    PurchaseOrder findByOrderNumber(String orderNumber);

    List<PurchaseOrder> findByPurchaseRequest(Long purchaseRequestId);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    // Issue Purchase Order to Supplier
    PurchaseOrder issuePurchaseOrder(
            Long purchaseOrderId,
            String issuedBy
    );

    // Cancel Purchase Order
    PurchaseOrder cancelPurchaseOrder(
            Long purchaseOrderId,
            String cancelledBy,
            String reason
    );
}