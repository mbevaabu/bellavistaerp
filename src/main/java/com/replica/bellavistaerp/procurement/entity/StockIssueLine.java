
package com.replica.bellavistaerp.procurement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_issue_lines")
@Getter
@Setter
@NoArgsConstructor
public class StockIssueLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_issue_id", nullable = false)
    private StockIssue stockIssue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_line_id", nullable = false)
    private ItemRequisitionLine requisitionLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_location_id", nullable = false)
    private StockLocation stockLocation;

    @Column(name = "quantity_issued", nullable = false, precision = 14, scale = 2)
    private BigDecimal quantityIssued;

    @Column(name = "unit_of_measure", nullable = false, length = 50)
    private String unitOfMeasure;

    @Column(length = 500)
    private String remarks;
}
