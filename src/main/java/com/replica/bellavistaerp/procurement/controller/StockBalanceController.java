package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.entity.StockBalance;
import com.replica.bellavistaerp.procurement.service.StockBalanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement/stock-balances")
@RequiredArgsConstructor
@Tag(name = "Stock Balances", description = "View stock quantities by item and location")
public class StockBalanceController {

    private final StockBalanceService stockBalanceService;

    @GetMapping
    @Operation(summary = "Get all stock balances")
    public List<StockBalance> findAll() {
        return stockBalanceService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stock balance by ID")
    public StockBalance findById(@PathVariable Long id) {
        return stockBalanceService.findById(id);
    }

    @GetMapping("/item/{itemId}")
    @Operation(summary = "Get stock balances for an item")
    public List<StockBalance> findByItem(@PathVariable Long itemId) {
        return stockBalanceService.findByItem(itemId);
    }

    @GetMapping("/location/{locationId}")
    @Operation(summary = "Get stock balances at a location")
    public List<StockBalance> findByLocation(@PathVariable Long locationId) {
        return stockBalanceService.findByLocation(locationId);
    }

    @GetMapping("/item/{itemId}/location/{locationId}")
    @Operation(summary = "Get stock balance for an item at a location")
    public StockBalance findByItemAndLocation(
            @PathVariable Long itemId,
            @PathVariable Long locationId
    ) {
        return stockBalanceService.findByItemAndLocation(itemId, locationId);
    }
}