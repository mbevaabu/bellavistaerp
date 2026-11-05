package com.replica.bellavistaerp.procurement.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "goods_received_voucher_lines")
@Getter
@Setter
@NoArgsConstructor
public class GoodsReceivedVoucherLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------------------------------------------------
    // GRV RELATIONSHIP
    // ---------------------------------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grv_id", nullable = false)
    private GoodsReceivedVoucher goodsReceivedVoucher;

    // ---------------------------------------------------------
    // PURCHASE ORDER TRACEABILITY
    // ---------------------------------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_line_id", nullable = false)
    private PurchaseOrderLine purchaseOrderLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // ---------------------------------------------------------
    // DESTINATION STOCK LOCATION
    // ---------------------------------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_location_id", nullable = false)
    private StockLocation stockLocation;

    // ---------------------------------------------------------
    // QUANTITY RECONCILIATION
    // ---------------------------------------------------------

    @Column(
            name = "quantity_ordered",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal quantityOrdered;

    @Column(
            name = "quantity_previously_received",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal quantityPreviouslyReceived = BigDecimal.ZERO;

    @Column(
            name = "quantity_delivered",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal quantityDelivered;

    @Column(
            name = "quantity_accepted",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal quantityAccepted = BigDecimal.ZERO;

    @Column(
            name = "quantity_rejected",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal quantityRejected = BigDecimal.ZERO;

    @Column(
            name = "quantity_damaged",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal quantityDamaged = BigDecimal.ZERO;

    @Column(
            name = "quantity_outstanding",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal quantityOutstanding = BigDecimal.ZERO;

    // ---------------------------------------------------------
    // UNIT AND INSPECTION DETAILS
    // ---------------------------------------------------------

    @Column(
            name = "unit_of_measure",
            nullable = false,
            length = 50
    )
    private String unitOfMeasure;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "inspection_remarks", length = 1000)
    private String inspectionRemarks;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;
}