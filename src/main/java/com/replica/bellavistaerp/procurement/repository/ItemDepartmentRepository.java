package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.ItemDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemDepartmentRepository
        extends JpaRepository<ItemDepartment, Long> {

    Optional<ItemDepartment> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}