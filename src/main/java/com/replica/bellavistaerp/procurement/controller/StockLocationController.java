package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.entity.StockLocation;
import com.replica.bellavistaerp.procurement.service.StockLocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement/stock-locations")
@RequiredArgsConstructor
@Tag(name = "Stock Locations", description = "Manage inventory storage locations")
public class StockLocationController {

    private final StockLocationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a stock location")
    public StockLocation create(
            @Valid @RequestBody StockLocation stockLocation) {
        return service.create(stockLocation);
    }

    @GetMapping
    @Operation(summary = "Get all stock locations")
    public List<StockLocation> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a stock location by ID")
    public StockLocation findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a stock location")
    public StockLocation update(
            @PathVariable Long id,
            @Valid @RequestBody StockLocation stockLocation) {
        return service.update(id, stockLocation);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a stock location")
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}