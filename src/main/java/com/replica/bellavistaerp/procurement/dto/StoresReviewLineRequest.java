package com.replica.bellavistaerp.procurement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class StoresReviewLineRequest {

    @NotNull(message = "Requisition line ID is required")
    private Long requisitionLineId;

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer quantityAvailable;

    @NotNull(message = "Recommended issue quantity is required")
    @Min(value = 0, message = "Recommended quantity cannot be negative")
    private Integer quantityRecommended;

    private String storesRemarks;
}