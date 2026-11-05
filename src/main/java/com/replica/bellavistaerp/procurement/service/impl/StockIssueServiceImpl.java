
package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.dto.StockIssueLineRequest;
import com.replica.bellavistaerp.procurement.entity.*;
import com.replica.bellavistaerp.procurement.enums.RequisitionStatus;
import com.replica.bellavistaerp.procurement.enums.StockMovementType;
import com.replica.bellavistaerp.procurement.repository.*;
import com.replica.bellavistaerp.procurement.service.StockIssueService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class StockIssueServiceImpl implements StockIssueService {

    private final StockIssueRepository stockIssueRepository;
    private final ItemRequisitionRepository requisitionRepository;
    private final StockBalanceRepository stockBalanceRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockLocationRepository stockLocationRepository;

    @Override
    public StockIssue issueStock(
            Long requisitionId,
            String issuedBy,
            String receivedBy,
            String remarks,
            List<StockIssueLineRequest> issueLines) {

        if (issueLines == null || issueLines.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one item must be included in the issue.");
        }

        if (issuedBy == null || issuedBy.isBlank()) {
            throw new IllegalArgumentException("Issued by is required.");
        }

        if (receivedBy == null || receivedBy.isBlank()) {
            throw new IllegalArgumentException("Received by is required.");
        }

        ItemRequisition requisition = requisitionRepository
                .findByIdForUpdate(requisitionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Requisition not found."));

        if (requisition.getStatus() != RequisitionStatus.READY_FOR_ISSUE
                && requisition.getStatus() != RequisitionStatus.PARTIALLY_ISSUED) {
            throw new IllegalStateException(
                    "Requisition must have Head and Operations Manager approval before stock can be issued.");
        }

        Set<Long> submittedLineIds = new HashSet<>();

        for (StockIssueLineRequest request : issueLines) {

            if (request.getRequisitionLineId() == null) {
                throw new IllegalArgumentException(
                        "Requisition line ID is required.");
            }

            if (!submittedLineIds.add(request.getRequisitionLineId())) {
                throw new IllegalArgumentException(
                        "A requisition line cannot appear more than once in one issue.");
            }

            if (request.getStockLocationId() == null) {
                throw new IllegalArgumentException(
                        "Stock location is required.");
            }

            if (request.getQuantityIssued() == null
                    || request.getQuantityIssued().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "Issue quantity must be greater than zero.");
            }
        }

        List<StockIssue> previousIssues =
                stockIssueRepository.findByRequisition_Id(requisitionId);

        Map<Long, BigDecimal> previouslyIssued = new HashMap<>();

        for (StockIssue previousIssue : previousIssues) {
            for (StockIssueLine previousLine : previousIssue.getItems()) {

                Long lineId = previousLine.getRequisitionLine().getId();

                previouslyIssued.merge(
                        lineId,
                        previousLine.getQuantityIssued(),
                        BigDecimal::add
                );
            }
        }

        StockIssue stockIssue = new StockIssue();
        stockIssue.setIssueNumber(generateIssueNumber());
        stockIssue.setRequisition(requisition);
        stockIssue.setIssuedBy(issuedBy.trim());
        stockIssue.setReceivedBy(receivedBy.trim());
        stockIssue.setRemarks(remarks);
        stockIssue.setIssueDate(LocalDateTime.now());

        for (StockIssueLineRequest request : issueLines) {

            ItemRequisitionLine requisitionLine = requisition.getItems()
                    .stream()
                    .filter(line -> line.getId().equals(
                            request.getRequisitionLineId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Requisition line does not belong to this requisition."));

            if (!requisitionLine.isStoresVerified()
                    || requisitionLine.getQuantityRecommended() == null) {
                throw new IllegalStateException(
                        "The requisition line must be reviewed by Stores before issue.");
            }

            BigDecimal requestedQuantity =
                    BigDecimal.valueOf(requisitionLine.getQuantity());

            BigDecimal recommendedQuantity =
                    BigDecimal.valueOf(requisitionLine.getQuantityRecommended());

            BigDecimal maximumQuantity = requestedQuantity.min(
                    recommendedQuantity);

            BigDecimal alreadyIssued = previouslyIssued.getOrDefault(
                    requisitionLine.getId(), BigDecimal.ZERO);

            BigDecimal remainingQuantity =
                    maximumQuantity.subtract(alreadyIssued);

            if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException(
                        "This requisition line has already received its recommended issue quantity.");
            }

            if (request.getQuantityIssued().compareTo(remainingQuantity) > 0) {
                throw new IllegalArgumentException(
                        "Issue quantity exceeds the remaining approved quantity for item: "
                                + requisitionLine.getItem().getName());
            }

            StockLocation location = stockLocationRepository
                    .findById(request.getStockLocationId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Stock location not found."));

            Item item = requisitionLine.getItem();

            StockBalance balance = stockBalanceRepository
                    .findByItemAndLocationForUpdate(
                            item.getId(), location.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "No stock balance exists for item "
                                    + item.getName()
                                    + " at the selected location."));

            if (balance.getQuantityOnHand() == null
                    || balance.getQuantityOnHand().compareTo(
                            request.getQuantityIssued()) < 0) {
                throw new IllegalStateException(
                        "Insufficient stock for item: " + item.getName());
            }

            balance.setQuantityOnHand(
                    balance.getQuantityOnHand().subtract(
                            request.getQuantityIssued())
            );

            stockBalanceRepository.save(balance);

            StockIssueLine issueLine = new StockIssueLine();
            issueLine.setRequisitionLine(requisitionLine);
            issueLine.setItem(item);
            issueLine.setStockLocation(location);
            issueLine.setQuantityIssued(request.getQuantityIssued());
            issueLine.setUnitOfMeasure(item.getUnitOfMeasure());
            issueLine.setRemarks(request.getRemarks());

            stockIssue.addItem(issueLine);

            StockMovement movement = new StockMovement();
            movement.setItem(item);
            movement.setMovementType(StockMovementType.ISSUE);
            movement.setSourceLocation(location);
            movement.setQuantity(request.getQuantityIssued());
            movement.setReferenceNumber(stockIssue.getIssueNumber());
            movement.setRemarks(
                    "Stock issued against requisition "
                            + requisition.getRequisitionNumber());
            movement.setPerformedBy(issuedBy.trim());

            stockMovementRepository.save(movement);

            previouslyIssued.merge(
                    requisitionLine.getId(),
                    request.getQuantityIssued(),
                    BigDecimal::add
            );
        }

        stockIssueRepository.save(stockIssue);

        // RESOLVED only when every original requested quantity is issued.
        boolean allLinesFullyIssued = requisition.getItems().stream()
                .allMatch(line -> {

                    BigDecimal requestedQuantity =
                            BigDecimal.valueOf(line.getQuantity());

                    BigDecimal issuedQuantity = previouslyIssued.getOrDefault(
                            line.getId(), BigDecimal.ZERO);

                    return issuedQuantity.compareTo(requestedQuantity) >= 0;
                });

        requisition.setStatus(
                allLinesFullyIssued
                        ? RequisitionStatus.RESOLVED
                        : RequisitionStatus.PARTIALLY_ISSUED
        );

        requisitionRepository.save(requisition);

        return stockIssue;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockIssue> findAll() {
        return stockIssueRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public StockIssue findById(Long id) {
        return stockIssueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stock issue not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockIssue> findByRequisition(Long requisitionId) {
        return stockIssueRepository
                .findByRequisition_IdOrderByIssueDateDesc(requisitionId);
    }

    @Override
    @Transactional(readOnly = true)
    public StockIssue findByIssueNumber(String issueNumber) {
        return stockIssueRepository.findByIssueNumber(issueNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stock issue not found."));
    }

    private String generateIssueNumber() {
        int year = Year.now().getValue();
        long nextNumber = stockIssueRepository.count() + 1;

        return "BVC-ISS-" + year + "-"
                + String.format("%05d", nextNumber);
    }
}
