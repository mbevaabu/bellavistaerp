package com.replica.bellavistaerp.procurement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GoodsReceivedVoucherLineRequest {

    @NotNull(message = "Purchase Order line ID is required.")
    private Long purchaseOrderLineId;

    @NotNull(message = "Destination stock location is required.")
    private Long stockLocationId;

    // ---------------------------------------------------------
    // QUANTITY DETAILS
    // ---------------------------------------------------------

    @NotNull(message = "Quantity delivered is required.")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Quantity delivered cannot be negative."
    )
    private BigDecimal quantityDelivered;

    @NotNull(message = "Quantity accepted is required.")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Quantity accepted cannot be negative."
    )
    private BigDecimal quantityAccepted;

    @NotNull(message = "Quantity rejected is required.")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Quantity rejected cannot be negative."
    )
    private BigDecimal quantityRejected;

    @NotNull(message = "Quantity damaged is required.")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Quantity damaged cannot be negative."
    )
    private BigDecimal quantityDamaged;

    // ---------------------------------------------------------
    // BATCH AND EXPIRY INFORMATION
    // ---------------------------------------------------------

    @Size(max = 100)
    private String batchNumber;

    private LocalDate expiryDate;

    // ---------------------------------------------------------
    // INSPECTION DETAILS
    // ---------------------------------------------------------

    @Size(max = 1000)
    private String inspectionRemarks;

    @Size(max = 1000)
    private String rejectionReason;
}