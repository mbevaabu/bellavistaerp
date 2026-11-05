package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.PurchaseOrder;
import com.replica.bellavistaerp.procurement.enums.PurchaseOrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    List<PurchaseOrder> findByPurchaseRequest_Id(Long purchaseRequestId);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    boolean existsByOrderNumber(String orderNumber);
}