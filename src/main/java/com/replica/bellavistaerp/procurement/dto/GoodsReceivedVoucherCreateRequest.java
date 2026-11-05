package com.replica.bellavistaerp.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GoodsReceivedVoucherCreateRequest {

    @NotNull(message = "Purchase Order ID is required.")
    private Long purchaseOrderId;

    // Stock Controller creating the GRV
    @NotBlank(message = "The creating officer's name is required.")
    @Size(max = 150)
    private String createdBy;

    // Physical receiving officer
    @NotBlank(message = "The receiving officer's name is required.")
    @Size(max = 150)
    private String receivedBy;

    @Size(max = 100)
    private String deliveryNoteNumber;

    private LocalDate deliveryDate;

    @Size(max = 50)
    private String vehicleRegistration;

    @Size(max = 150)
    private String driverName;

    @Size(max = 200)
    private String deliveryLocation;

    @Size(max = 1000)
    private String remarks;

    @Valid
    @NotEmpty(message = "At least one delivered item is required.")
    private List<GoodsReceivedVoucherLineRequest> items =
            new ArrayList<>();
}