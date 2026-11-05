package com.replica.bellavistaerp.procurement.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemDepartmentResponse {

    private Long id;
    private String name;
    private String description;
    private boolean active;
}