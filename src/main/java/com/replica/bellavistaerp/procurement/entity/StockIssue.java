
package com.replica.bellavistaerp.procurement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_issues")
@Getter
@Setter
@NoArgsConstructor
public class StockIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issue_number", nullable = false, unique = true, length = 40)
    private String issueNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", nullable = false)
    private ItemRequisition requisition;

    @Column(name = "issued_by", nullable = false, length = 150)
    private String issuedBy;

    @Column(name = "received_by", nullable = false, length = 150)
    private String receivedBy;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(length = 1000)
    private String remarks;

    @OneToMany(
            mappedBy = "stockIssue",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StockIssueLine> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (issueDate == null) {
            issueDate = LocalDateTime.now();
        }
    }

    public void addItem(StockIssueLine item) {
        items.add(item);
        item.setStockIssue(this);
    }

    public void removeItem(StockIssueLine item) {
        items.remove(item);
        item.setStockIssue(null);
    }
}
