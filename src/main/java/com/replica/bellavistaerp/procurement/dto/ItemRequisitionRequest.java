package com.replica.bellavistaerp.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemRequisitionRequest {

    @NotBlank(message = "Purpose is required")
    @Size(max = 1000)
    private String purpose;

    @NotEmpty(message = "At least one item must be requested")
    @Valid
    private List<ItemRequisitionLineRequest> items;
}