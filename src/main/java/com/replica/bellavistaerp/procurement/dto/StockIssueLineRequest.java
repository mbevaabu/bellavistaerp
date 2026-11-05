
package com.replica.bellavistaerp.procurement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class StockIssueLineRequest {

    @NotNull(message = "Requisition line ID is required")
    private Long requisitionLineId;

    @NotNull(message = "Stock location is required")
    private Long stockLocationId;

    @NotNull(message = "Quantity issued is required")
    @DecimalMin(value = "0.01", message = "Quantity issued must be greater than zero")
    private BigDecimal quantityIssued;

    private String remarks;
}
