package com.replica.bellavistaerp.procurement.controller;

import com.replica.bellavistaerp.procurement.dto.ItemDepartmentRequest;
import com.replica.bellavistaerp.procurement.dto.ItemDepartmentResponse;
import com.replica.bellavistaerp.procurement.service.ItemDepartmentService;

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
@RequestMapping("/api/procurement/departments")
@RequiredArgsConstructor
@Tag(
    name = "Item Departments",
    description = "Manage inventory departments at Bella Vista College"
)
public class ItemDepartmentController {

    private final ItemDepartmentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create an item department",
        description = "Creates a department such as Food & Kitchen, Cleaning or Stationery."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Department successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid department details")
    })
    public ItemDepartmentResponse create(
            @Valid @RequestBody ItemDepartmentRequest request) {
        return service.create(request);
    }

    @GetMapping
    @Operation(summary = "Retrieve all item departments")
    @ApiResponse(responseCode = "200", description = "Departments successfully retrieved")
    public List<ItemDepartmentResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve an item department by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Department successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ItemDepartmentResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an item department")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Department successfully updated"),
        @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ItemDepartmentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ItemDepartmentRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Deactivate an item department",
        description = "Deactivates the department without deleting its historical records."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Department successfully deactivated"),
        @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}