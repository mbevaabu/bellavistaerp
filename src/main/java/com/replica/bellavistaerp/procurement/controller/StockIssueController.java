
package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.dto.StockIssueLineRequest;
import com.replica.bellavistaerp.procurement.entity.StockIssue;
import com.replica.bellavistaerp.procurement.service.StockIssueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement/stock-issues")
@RequiredArgsConstructor
@Tag(name = "Stock Issues", description = "Approved requisition stock issue operations")
public class StockIssueController {

    private final StockIssueService stockIssueService;

    @PostMapping("/requisition/{requisitionId}")
    @Operation(summary = "Issue stock against an approved requisition")
    public StockIssue issueStock(
            @PathVariable Long requisitionId,
            @RequestParam String issuedBy,
            @RequestParam String receivedBy,
            @RequestParam(required = false) String remarks,
            @Valid @RequestBody List<StockIssueLineRequest> issueLines) {

        return stockIssueService.issueStock(
                requisitionId,
                issuedBy,
                receivedBy,
                remarks,
                issueLines
        );
    }

    @GetMapping
    @Operation(summary = "Get all stock issues")
    public List<StockIssue> findAll() {
        return stockIssueService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stock issue by ID")
    public StockIssue findById(@PathVariable Long id) {
        return stockIssueService.findById(id);
    }

    @GetMapping("/requisition/{requisitionId}")
    @Operation(summary = "Get stock issues for a requisition")
    public List<StockIssue> findByRequisition(
            @PathVariable Long requisitionId) {
        return stockIssueService.findByRequisition(requisitionId);
    }

    @GetMapping("/number/{issueNumber}")
    @Operation(summary = "Get stock issue by issue number")
    public StockIssue findByIssueNumber(
            @PathVariable String issueNumber) {
        return stockIssueService.findByIssueNumber(issueNumber);
    }
}
