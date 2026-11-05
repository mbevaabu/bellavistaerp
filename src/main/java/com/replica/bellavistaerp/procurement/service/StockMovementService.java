package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.entity.StockMovement;
import com.replica.bellavistaerp.procurement.enums.StockMovementType;

import java.math.BigDecimal;
import java.util.List;

public interface StockMovementService {

    // Stock movement history
    List<StockMovement> findAll();

    StockMovement findById(Long id);

    List<StockMovement> findByItem(Long itemId);

    List<StockMovement> findByMovementType(
            StockMovementType movementType
    );

    List<StockMovement> findByReferenceNumber(String referenceNumber);

    // Business operations
    StockMovement receiveStock(
            Long itemId,
            Long locationId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks,
            String performedBy
    );

    StockMovement issueStock(
            Long itemId,
            Long locationId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks,
            String performedBy
    );

    StockMovement transferStock(
            Long itemId,
            Long sourceLocationId,
            Long destinationLocationId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks,
            String performedBy
    );

    StockMovement adjustStock(
            Long itemId,
            Long locationId,
            BigDecimal quantity,
            boolean increase,
            String referenceNumber,
            String remarks,
            String performedBy
    );
}