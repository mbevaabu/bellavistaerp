
package com.replica.bellavistaerp.procurement.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_requisition_lines")
@Getter
@Setter
@NoArgsConstructor
public class ItemRequisitionLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", nullable = false)
    private ItemRequisition requisition;

    // Link to the actual inventory item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 50)
    private String unitOfMeasure;

    // Stores assessment
    @Column(name = "quantity_available")
    private Integer quantityAvailable;

    @Column(name = "quantity_recommended")
    private Integer quantityRecommended;

    @Column(name = "quantity_shortage")
    private Integer quantityShortage;

    @Column(name = "stores_remarks", length = 1000)
    private String storesRemarks;

    @Column(name = "stores_verified")
    private boolean storesVerified = false;
}
