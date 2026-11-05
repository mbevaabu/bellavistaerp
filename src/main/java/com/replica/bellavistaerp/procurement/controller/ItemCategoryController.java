package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.dto.ItemCategoryRequest;
import com.replica.bellavistaerp.procurement.dto.ItemCategoryResponse;
import com.replica.bellavistaerp.procurement.service.ItemCategoryService;

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
@RequestMapping("/api/procurement/categories")
@RequiredArgsConstructor
@Tag(
    name = "Item Categories",
    description = "Manage inventory categories within Bella Vista College departments"
)
public class ItemCategoryController {

    private final ItemCategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create an item category",
        description = "Creates a category under an existing item department."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Category successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid category details"),
        @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ItemCategoryResponse create(
            @Valid @RequestBody ItemCategoryRequest request) {
        return service.create(request);
    }

    @GetMapping
    @Operation(summary = "Retrieve all item categories")
    @ApiResponse(responseCode = "200", description = "Categories successfully retrieved")
    public List<ItemCategoryResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a category by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ItemCategoryResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Retrieve categories belonging to a department")
    public List<ItemCategoryResponse> findByDepartment(
            @PathVariable Long departmentId) {
        return service.findByDepartment(departmentId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an item category")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category successfully updated"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ItemCategoryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ItemCategoryRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Deactivate an item category",
        description = "Deactivates a category without deleting historical records."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Category successfully deactivated"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}
