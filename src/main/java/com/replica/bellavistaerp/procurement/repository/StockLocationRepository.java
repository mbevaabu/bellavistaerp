package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.StockLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockLocationRepository
        extends JpaRepository<StockLocation, Long> {

    Optional<StockLocation> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}