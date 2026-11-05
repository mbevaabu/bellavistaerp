package com.replica.bellavistaerp.procurement.entity;

import com.replica.bellavistaerp.procurement.enums.PurchaseRequestStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_requests")
@Getter
@Setter
@NoArgsConstructor
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_number", nullable = false, unique = true, length = 50)
    private String requestNumber;

    // ORIGINAL ITEM REQUISITION

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", nullable = false, unique = true)
    private ItemRequisition requisition;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "requested_by", nullable = false, length = 150)
    private String requestedBy;

    @Column(name = "requesting_department", length = 150)
    private String requestingDepartment;

    @Column(length = 1000)
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PurchaseRequestStatus status;

    @Column(name = "estimated_total", precision = 14, scale = 2)
    private BigDecimal estimatedTotal = BigDecimal.ZERO;


    // OPERATIONS MANAGER REVIEW

    @Column(name = "operations_manager", length = 150)
    private String operationsManager;

    @Column(name = "om_comments", length = 1000)
    private String omComments;

    @Column(name = "om_review_date")
    private LocalDateTime omReviewDate;


    // OD1 APPROVAL

    @Column(name = "od1_approved_by", length = 150)
    private String od1ApprovedBy;

    @Column(name = "od1_comments", length = 1000)
    private String od1Comments;

    @Column(name = "od1_approval_date")
    private LocalDateTime od1ApprovalDate;


    // OD1 REJECTION

    @Column(name = "od1_rejected_by", length = 150)
    private String od1RejectedBy;

    @Column(name = "od1_rejection_comments", length = 1000)
    private String od1RejectionComments;

    @Column(name = "od1_rejection_date")
    private LocalDateTime od1RejectionDate;


    // OD2 APPROVAL

    @Column(name = "od2_approved_by", length = 150)
    private String od2ApprovedBy;

    @Column(name = "od2_comments", length = 1000)
    private String od2Comments;

    @Column(name = "od2_approval_date")
    private LocalDateTime od2ApprovalDate;


    // OD2 REJECTION

    @Column(name = "od2_rejected_by", length = 150)
    private String od2RejectedBy;

    @Column(name = "od2_rejection_comments", length = 1000)
    private String od2RejectionComments;

    @Column(name = "od2_rejection_date")
    private LocalDateTime od2RejectionDate;


    // RESPONSIBLE AUTHORITY APPROVAL

    @Column(name = "responsible_authority", length = 150)
    private String responsibleAuthority;

    @Column(name = "ra_comments", length = 1000)
    private String raComments;

    @Column(name = "ra_approval_date")
    private LocalDateTime raApprovalDate;


    // RESPONSIBLE AUTHORITY REJECTION

    @Column(name = "ra_rejected_by", length = 150)
    private String raRejectedBy;

    @Column(name = "ra_rejection_comments", length = 1000)
    private String raRejectionComments;

    @Column(name = "ra_rejection_date")
    private LocalDateTime raRejectionDate;


    // PURCHASE REQUEST ITEMS

    @OneToMany(
        mappedBy = "purchaseRequest",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PurchaseRequestLine> items = new ArrayList<>();


    // AUTOMATIC INITIALISATION

    @PrePersist
    protected void onCreate() {

        if (requestDate == null) {
            requestDate = LocalDateTime.now();
        }

        if (status == null) {
            status = PurchaseRequestStatus.DRAFT;
        }

        if (estimatedTotal == null) {
            estimatedTotal = BigDecimal.ZERO;
        }
    }


    // ITEM MANAGEMENT

    public void addItem(PurchaseRequestLine item) {
        items.add(item);
        item.setPurchaseRequest(this);
    }

    public void removeItem(PurchaseRequestLine item) {
        items.remove(item);
        item.setPurchaseRequest(null);
    }
}