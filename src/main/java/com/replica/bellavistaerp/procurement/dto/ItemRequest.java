package com.replica.bellavistaerp.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ItemRequest {

    @NotBlank
    @Size(max = 30)
    private String itemCode;

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(max = 50)
    private String unitOfMeasure;

    @NotNull(message = "Please select an item category")
    private Long categoryId;

    @NotNull(message = "Minimum stock level is required")
    @DecimalMin(value = "0.0", message = "Minimum stock level cannot be negative")
    private BigDecimal minimumStockLevel = BigDecimal.ZERO;

    @NotNull(message = "Reorder level is required")
    @DecimalMin(value = "0.0", message = "Reorder level cannot be negative")
    private BigDecimal reorderLevel = BigDecimal.ZERO;
}