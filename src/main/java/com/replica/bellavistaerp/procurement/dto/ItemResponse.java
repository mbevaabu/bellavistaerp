package com.replica.bellavistaerp.procurement.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ItemResponse {

    private Long id;
    private String itemCode;
    private String name;
    private String description;
    private String unitOfMeasure;

    private Long categoryId;
    private String categoryName;
    private String departmentName;

    private BigDecimal minimumStockLevel;
    private BigDecimal reorderLevel;

    private boolean active;
}