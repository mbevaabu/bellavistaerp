package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemCategoryRepository
        extends JpaRepository<ItemCategory, Long> {

    List<ItemCategory> findByDepartmentId(Long departmentId);

    boolean existsByNameAndDepartmentId(
            String name, Long departmentId);

    boolean existsByNameAndDepartmentIdAndIdNot(
            String name, Long departmentId, Long id);
}