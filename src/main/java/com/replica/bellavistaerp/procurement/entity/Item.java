package com.replica.bellavistaerp.procurement.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code", nullable = false, unique = true, length = 30)
    private String itemCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "unit_of_measure", nullable = false, length = 50)
    private String unitOfMeasure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ItemCategory category;

    @Column(name = "minimum_stock_level", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal minimumStockLevel = BigDecimal.ZERO;

    @Column(name = "reorder_level", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

   
}