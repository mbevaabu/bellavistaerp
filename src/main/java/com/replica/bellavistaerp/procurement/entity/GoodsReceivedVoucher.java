package com.replica.bellavistaerp.procurement.entity;

import com.replica.bellavistaerp.procurement.enums.GoodsReceivedVoucherStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goods_received_vouchers")
@Getter
@Setter
@NoArgsConstructor
public class GoodsReceivedVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "grv_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String grvNumber;

    // ---------------------------------------------------------
    // PURCHASE ORDER AND SUPPLIER DETAILS
    // ---------------------------------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "supplier_name", nullable = false, length = 200)
    private String supplierName;

    @Column(name = "delivery_note_number", length = 100)
    private String deliveryNoteNumber;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "received_date", nullable = false)
    private LocalDateTime receivedDate;

    // ---------------------------------------------------------
    // GRV CREATION AND PHYSICAL RECEIVING
    // ---------------------------------------------------------

    @Column(name = "created_by", nullable = false, length = 150)
    private String createdBy;

    @Column(name = "received_by", nullable = false, length = 150)
    private String receivedBy;

    @Column(name = "vehicle_registration", length = 50)
    private String vehicleRegistration;

    @Column(name = "driver_name", length = 150)
    private String driverName;

    @Column(name = "delivery_location", length = 200)
    private String deliveryLocation;


    // ---------------------------------------------------------
// SUBMISSION FOR VERIFICATION
// ---------------------------------------------------------

@Column(name = "submitted_by", length = 150)
private String submittedBy;

@Column(name = "submission_date")
private LocalDateTime submissionDate;

    // ---------------------------------------------------------
    // STOCK CONTROLLER VERIFICATION
    // ---------------------------------------------------------

    @Column(name = "verified_by", length = 150)
    private String verifiedBy;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    @Column(name = "verification_remarks", length = 1000)
    private String verificationRemarks;

    // ---------------------------------------------------------
    // OPERATIONS MANAGER APPROVAL
    // ---------------------------------------------------------

    @Column(name = "approved_by", length = 150)
    private String approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approval_comments", length = 1000)
    private String approvalComments;

    // ---------------------------------------------------------
    // REJECTION INFORMATION
    // ---------------------------------------------------------

    @Column(name = "rejected_by", length = 150)
    private String rejectedBy;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "rejection_date")
    private LocalDateTime rejectionDate;

    // ---------------------------------------------------------
    // GENERAL INFORMATION
    // ---------------------------------------------------------

    @Column(length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private GoodsReceivedVoucherStatus status;

    @OneToMany(
            mappedBy = "goodsReceivedVoucher",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<GoodsReceivedVoucherLine> items = new ArrayList<>();

    // ---------------------------------------------------------
    // RELATIONSHIP MANAGEMENT
    // ---------------------------------------------------------

    public void addItem(GoodsReceivedVoucherLine item) {

        items.add(item);
        item.setGoodsReceivedVoucher(this);
    }

    public void removeItem(GoodsReceivedVoucherLine item) {

        items.remove(item);
        item.setGoodsReceivedVoucher(null);
    }

    // ---------------------------------------------------------
    // AUTOMATIC INITIALISATION
    // ---------------------------------------------------------

    @PrePersist
    protected void onCreate() {

        if (receivedDate == null) {
            receivedDate = LocalDateTime.now();
        }

        if (status == null) {
            status = GoodsReceivedVoucherStatus.DRAFT;
        }
    }
}