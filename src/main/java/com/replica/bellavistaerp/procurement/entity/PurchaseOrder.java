package com.replica.bellavistaerp.procurement.entity;

import com.replica.bellavistaerp.procurement.enums.PurchaseOrderStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "order_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;

    // Supplier information

    @Column(name = "supplier_name", nullable = false, length = 200)
    private String supplierName;

    @Column(name = "supplier_contact", length = 100)
    private String supplierContact;

    @Column(name = "supplier_address", length = 500)
    private String supplierAddress;

    // Order details

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column(name = "delivery_location", length = 200)
    private String deliveryLocation;

    @Column(name = "payment_terms", length = 500)
    private String paymentTerms;

    @Column(length = 1000)
    private String remarks;

    // Financial details

    @Column(name = "sub_total", precision = 14, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 14, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 14, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Order status

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PurchaseOrderStatus status;

    // Administration preparation and authorisation

    @Column(name = "prepared_by", nullable = false, length = 150)
    private String preparedBy;

    @Column(name = "issued_by", length = 150)
    private String issuedBy;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate;

    // Purchase order lines

    @OneToMany(
            mappedBy = "purchaseOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PurchaseOrderLine> items = new ArrayList<>();

    // Automatically initialise new orders

    @PrePersist
    protected void onCreate() {

        if (orderDate == null) {
            orderDate = LocalDate.now();
        }

        if (status == null) {
            status = PurchaseOrderStatus.DRAFT;
        }

        if (subTotal == null) {
            subTotal = BigDecimal.ZERO;
        }

        if (taxAmount == null) {
            taxAmount = BigDecimal.ZERO;
        }

        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
    }

    // Maintain relationship with purchase order lines

    public void addItem(PurchaseOrderLine item) {

        items.add(item);
        item.setPurchaseOrder(this);
    }

    public void removeItem(PurchaseOrderLine item) {

        items.remove(item);
        item.setPurchaseOrder(null);
    }
}