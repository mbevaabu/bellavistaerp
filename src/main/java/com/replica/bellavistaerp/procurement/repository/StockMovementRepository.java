package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.StockMovement;
import com.replica.bellavistaerp.procurement.enums.StockMovementType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByItem_Id(Long itemId);

    List<StockMovement> findByMovementType(StockMovementType movementType);

    List<StockMovement> findByReferenceNumber(String referenceNumber);

    List<StockMovement> findByItem_IdOrderByMovementDateDesc(Long itemId);
}