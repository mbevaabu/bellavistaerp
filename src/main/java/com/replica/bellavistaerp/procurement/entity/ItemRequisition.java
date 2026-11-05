package com.replica.bellavistaerp.procurement.entity;

import com.replica.bellavistaerp.procurement.enums.RequisitionStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item_requisitions")
@Getter
@Setter
@NoArgsConstructor
public class ItemRequisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "requisition_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String requisitionNumber;

    @Column(name = "requesting_department", nullable = false, length = 100)
    private String requestingDepartment;

    @Column(name = "requesting_officer", nullable = false, length = 150)
    private String requestingOfficer;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(nullable = false, length = 1000)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RequisitionStatus status;

    // Stock Controller verification and submission

    @Column(name = "stock_controller", length = 150)
    private String stockController;

    @Column(name = "submitted_for_approval_at")
    private LocalDateTime submittedForApprovalAt;

    // Head approval

    @Column(name = "head_approved_by", length = 150)
    private String headApprovedBy;

    @Column(name = "head_approval_comments", length = 1000)
    private String headApprovalComments;

    @Column(name = "head_approval_date")
    private LocalDateTime headApprovalDate;

    // Operations Manager approval

    @Column(name = "operations_manager_approved_by", length = 150)
    private String operationsManagerApprovedBy;

    @Column(name = "operations_manager_comments", length = 1000)
    private String operationsManagerComments;

    @Column(name = "operations_manager_approval_date")
    private LocalDateTime operationsManagerApprovalDate;

    // Requisition items

    @OneToMany(
            mappedBy = "requisition",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemRequisitionLine> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (requestDate == null) {
            requestDate = LocalDateTime.now();
        }

        if (status == null) {
            status = RequisitionStatus.NEW;
        }
    }

    public void addItem(ItemRequisitionLine item) {
        items.add(item);
        item.setRequisition(this);
    }

    public void removeItem(ItemRequisitionLine item) {
        items.remove(item);
        item.setRequisition(null);
    }
}