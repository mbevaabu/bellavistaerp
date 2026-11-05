package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.PurchaseOrderLine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderLineRepository
        extends JpaRepository<PurchaseOrderLine, Long> {

    List<PurchaseOrderLine> findByPurchaseOrder_Id(Long purchaseOrderId);

    List<PurchaseOrderLine> findByItem_Id(Long itemId);

    List<PurchaseOrderLine> findByPurchaseRequestLine_Id(Long purchaseRequestLineId);
}