package com.replica.bellavistaerp.procurement.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
    name = "item_categories",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_category_department_name",
            columnNames = {"department_id", "name"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ItemCategory {

   @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private ItemDepartment department;

    @Column(nullable = false)
    private boolean active = true;
}