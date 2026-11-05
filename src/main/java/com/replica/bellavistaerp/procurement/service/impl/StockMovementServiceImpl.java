package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.entity.Item;
import com.replica.bellavistaerp.procurement.entity.StockBalance;
import com.replica.bellavistaerp.procurement.entity.StockLocation;
import com.replica.bellavistaerp.procurement.entity.StockMovement;
import com.replica.bellavistaerp.procurement.enums.StockMovementType;
import com.replica.bellavistaerp.procurement.repository.ItemRepository;
import com.replica.bellavistaerp.procurement.repository.StockBalanceRepository;
import com.replica.bellavistaerp.procurement.repository.StockLocationRepository;
import com.replica.bellavistaerp.procurement.repository.StockMovementRepository;
import com.replica.bellavistaerp.procurement.service.StockMovementService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository movementRepository;
    private final StockBalanceRepository balanceRepository;
    private final ItemRepository itemRepository;
    private final StockLocationRepository locationRepository;

    // ------------------------------------------------
    // STOCK MOVEMENT HISTORY
    // ------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> findAll() {
        return movementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public StockMovement findById(Long id) {
        return movementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Stock movement not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> findByItem(Long itemId) {
        return movementRepository
                .findByItem_IdOrderByMovementDateDesc(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> findByMovementType(
            StockMovementType movementType) {

        return movementRepository.findByMovementType(movementType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> findByReferenceNumber(String referenceNumber) {
        return movementRepository.findByReferenceNumber(referenceNumber);
    }

    // ------------------------------------------------
    // RECEIVE STOCK
    // ------------------------------------------------

    @Override
    public StockMovement receiveStock(
            Long itemId,
            Long locationId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks,
            String performedBy) {

        validateMovement(quantity, performedBy);

        Item item = getItem(itemId);
        StockLocation location = getLocation(locationId);

        StockBalance balance = getOrCreateBalance(item, location);

        balance.setQuantityOnHand(
                balance.getQuantityOnHand().add(quantity)
        );

        balanceRepository.save(balance);

        return saveMovement(
                item,
                StockMovementType.RECEIPT,
                null,
                location,
                quantity,
                referenceNumber,
                remarks,
                performedBy
        );
    }

    // ------------------------------------------------
    // ISSUE STOCK
    // ------------------------------------------------

    @Override
    public StockMovement issueStock(
            Long itemId,
            Long locationId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks,
            String performedBy) {

        validateMovement(quantity, performedBy);

        Item item = getItem(itemId);
        StockLocation location = getLocation(locationId);

        StockBalance balance = getExistingBalanceForUpdate(itemId, locationId);

        ensureSufficientStock(balance, quantity);

        balance.setQuantityOnHand(
                balance.getQuantityOnHand().subtract(quantity)
        );

        balanceRepository.save(balance);

        return saveMovement(
                item,
                StockMovementType.ISSUE,
                location,
                null,
                quantity,
                referenceNumber,
                remarks,
                performedBy
        );
    }

    // ------------------------------------------------
    // TRANSFER STOCK
    // ------------------------------------------------

    @Override
    public StockMovement transferStock(
            Long itemId,
            Long sourceLocationId,
            Long destinationLocationId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks,
            String performedBy) {

        validateMovement(quantity, performedBy);

        if (sourceLocationId.equals(destinationLocationId)) {
            throw new IllegalArgumentException(
                    "Source and destination locations must be different."
            );
        }

        Item item = getItem(itemId);

        StockLocation source = getLocation(sourceLocationId);
        StockLocation destination = getLocation(destinationLocationId);

        StockBalance sourceBalance =
                getExistingBalanceForUpdate(itemId, sourceLocationId);

        ensureSufficientStock(sourceBalance, quantity);

        StockBalance destinationBalance =
                getOrCreateBalance(item, destination);

        sourceBalance.setQuantityOnHand(
                sourceBalance.getQuantityOnHand().subtract(quantity)
        );

        destinationBalance.setQuantityOnHand(
                destinationBalance.getQuantityOnHand().add(quantity)
        );

        balanceRepository.save(sourceBalance);
        balanceRepository.save(destinationBalance);

        return saveMovement(
                item,
                StockMovementType.TRANSFER,
                source,
                destination,
                quantity,
                referenceNumber,
                remarks,
                performedBy
        );
    }

    // ------------------------------------------------
    // STOCK ADJUSTMENT
    // ------------------------------------------------

    @Override
    public StockMovement adjustStock(
            Long itemId,
            Long locationId,
            BigDecimal quantity,
            boolean increase,
            String referenceNumber,
            String remarks,
            String performedBy) {

        validateMovement(quantity, performedBy);

        Item item = getItem(itemId);
        StockLocation location = getLocation(locationId);

        StockBalance balance = getOrCreateBalance(item, location);

        if (increase) {
            balance.setQuantityOnHand(
                    balance.getQuantityOnHand().add(quantity)
            );
        } else {
            ensureSufficientStock(balance, quantity);

            balance.setQuantityOnHand(
                    balance.getQuantityOnHand().subtract(quantity)
            );
        }

        balanceRepository.save(balance);

        return saveMovement(
                item,
                StockMovementType.ADJUSTMENT,
                increase ? null : location,
                increase ? location : null,
                quantity,
                referenceNumber,
                remarks,
                performedBy
        );
    }

    // ------------------------------------------------
    // PRIVATE HELPER METHODS
    // ------------------------------------------------

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found: " + itemId));
    }

    private StockLocation getLocation(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Stock location not found: " + locationId
                        ));
    }

    private StockBalance getOrCreateBalance(
            Item item,
            StockLocation location) {

        return balanceRepository
                .findByItemAndLocationForUpdate(item.getId(), location.getId())
                .orElseGet(() -> {
                    StockBalance balance = new StockBalance();
                    balance.setItem(item);
                    balance.setLocation(location);
                    balance.setQuantityOnHand(BigDecimal.ZERO);
                    return balance;
                });
    }

    private StockBalance getExistingBalanceForUpdate(
            Long itemId,
            Long locationId) {

        return balanceRepository
                .findByItemAndLocationForUpdate(itemId, locationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No stock balance exists for item "
                                        + itemId + " at location " + locationId
                        ));
    }

    private void ensureSufficientStock(
            StockBalance balance,
            BigDecimal quantity) {

        if (balance.getQuantityOnHand().compareTo(quantity) < 0) {
            throw new IllegalStateException(
                    "Insufficient stock. Available: "
                            + balance.getQuantityOnHand()
                            + ", requested: " + quantity
            );
        }
    }

    private void validateMovement(
            BigDecimal quantity,
            String performedBy) {

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Movement quantity must be greater than zero."
            );
        }

        if (performedBy == null || performedBy.isBlank()) {
            throw new IllegalArgumentException(
                    "The person performing the movement must be specified."
            );
        }
    }

    private StockMovement saveMovement(
            Item item,
            StockMovementType type,
            StockLocation source,
            StockLocation destination,
            BigDecimal quantity,
            String referenceNumber,
            String remarks,
            String performedBy) {

        StockMovement movement = new StockMovement();

        movement.setItem(item);
        movement.setMovementType(type);
        movement.setSourceLocation(source);
        movement.setDestinationLocation(destination);
        movement.setQuantity(quantity);
        movement.setReferenceNumber(referenceNumber);
        movement.setRemarks(remarks);
        movement.setPerformedBy(performedBy);

        return movementRepository.save(movement);
    }
}