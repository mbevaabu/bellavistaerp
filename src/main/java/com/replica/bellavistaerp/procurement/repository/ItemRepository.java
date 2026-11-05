package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByItemCode(String itemCode);

    boolean existsByItemCode(String itemCode);

    List<Item> findByCategoryId(Long categoryId);

    List<Item> findByCategoryDepartmentId(Long departmentId);

    boolean existsByCategoryIdAndActiveTrue(Long categoryId);
}