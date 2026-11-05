package com.replica.bellavistaerp.procurement.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "stock_balances",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_stock_balance_item_location",
            columnNames = {"item_id", "location_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class StockBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private StockLocation location;

    @Column(
        name = "quantity_on_hand",
        nullable = false,
        precision = 14,
        scale = 2
    )
    private BigDecimal quantityOnHand = BigDecimal.ZERO;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void updateTimestamp() {
        lastUpdated = LocalDateTime.now();
    }
}