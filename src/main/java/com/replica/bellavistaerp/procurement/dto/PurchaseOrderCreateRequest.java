package com.replica.bellavistaerp.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrderCreateRequest {

    @NotNull(message = "Purchase Request ID is required")
    private Long purchaseRequestId;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 200)
    private String supplierName;

    @Size(max = 100)
    private String supplierContact;

    @Size(max = 500)
    private String supplierAddress;

    private LocalDate expectedDeliveryDate;

    @Size(max = 200)
    private String deliveryLocation;

    @Size(max = 500)
    private String paymentTerms;

    @Size(max = 1000)
    private String remarks;

    @NotBlank(message = "Prepared by is required")
    @Size(max = 150)
    private String preparedBy;

    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.00", message = "Tax amount cannot be negative")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Valid
    @NotEmpty(message = "At least one purchase order line is required")
    private List<PurchaseOrderLineRequest> items = new ArrayList<>();
}