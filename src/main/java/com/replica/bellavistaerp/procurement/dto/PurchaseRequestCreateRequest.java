package com.replica.bellavistaerp.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseRequestCreateRequest {

    @NotNull(message = "Requisition ID is required")
    private Long requisitionId;

    @NotBlank(message = "Requested by is required")
    @Size(max = 150)
    private String requestedBy;

    @Size(max = 1000)
    private String justification;

    @Valid
    @NotEmpty(message = "At least one purchase request line is required")
    private List<PurchaseRequestLineRequest> items = new ArrayList<>();
}