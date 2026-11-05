package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.PurchaseRequestLine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRequestLineRepository extends JpaRepository<PurchaseRequestLine, Long> {

    List<PurchaseRequestLine> findByPurchaseRequest_Id(Long purchaseRequestId);

    List<PurchaseRequestLine> findByItem_Id(Long itemId);
}