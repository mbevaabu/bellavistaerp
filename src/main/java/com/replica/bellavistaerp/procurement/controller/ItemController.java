package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.dto.ItemRequest;
import com.replica.bellavistaerp.procurement.dto.ItemResponse;
import com.replica.bellavistaerp.procurement.service.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement/items")
@RequiredArgsConstructor
@Tag(
    name = "Item Catalogue",
    description = "Manage Bella Vista College inventory items and classifications"
)
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Register a new stock item",
        description = """
            Registers an item in the Bella Vista College stock catalogue.
            Each item must have a unique item code and belong to an
            existing item category.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Item successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid request or duplicate item code"),
        @ApiResponse(responseCode = "404", description = "Item category not found")
    })
    public ItemResponse create(@Valid @RequestBody ItemRequest request) {
        return itemService.create(request);
    }

    @GetMapping
    @Operation(
        summary = "Retrieve all stock items",
        description = "Returns all registered items in the Bella Vista College catalogue."
    )
    @ApiResponse(responseCode = "200", description = "Items successfully retrieved")
    public List<ItemResponse> findAll() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Retrieve an item by ID",
        description = "Retrieves the complete details of a specific stock item."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ItemResponse findById(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(
        summary = "Retrieve items by category",
        description = "Returns all stock items belonging to a selected item category."
    )
    @ApiResponse(responseCode = "200", description = "Category items successfully retrieved")
    public List<ItemResponse> findByCategory(
            @PathVariable Long categoryId) {
        return itemService.findByCategory(categoryId);
    }

    @GetMapping("/department/{departmentId}")
    @Operation(
        summary = "Retrieve items by department",
        description = "Returns all stock items belonging to a selected item department."
    )
    @ApiResponse(responseCode = "200", description = "Department items successfully retrieved")
    public List<ItemResponse> findByDepartment(
            @PathVariable Long departmentId) {
        return itemService.findByDepartment(departmentId);
    }
}