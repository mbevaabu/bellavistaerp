package com.replica.bellavistaerp.procurement.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemCategoryResponse {

    private Long id;
    private String name;
    private String description;

    private Long departmentId;
    private String departmentName;

    private boolean active;
}