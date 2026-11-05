package com.replica.bellavistaerp.procurement.config;

import com.replica.bellavistaerp.procurement.entity.StockLocation;
import com.replica.bellavistaerp.procurement.repository.StockLocationRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StockLocationInitializer {

    private final StockLocationRepository repository;

    @Bean
    CommandLineRunner initialiseStockLocations() {
        return args -> {
            createLocation(
                    "MAIN_STORE",
                    "Central store for general supplies and food"
            );

            createLocation(
                    "KITCHEN_PANTRY",
                    "Kitchen food storage managed by the Chef"
            );

            createLocation(
                    "CLEANING_SUPPLIES",
                    "Storage for detergents and cleaning materials"
            );

            createLocation(
                    "UNIFORMS_CENTRE",
                    "Storage and sales point for school uniforms"
            );
        };
    }

    private void createLocation(String name, String description) {

        if (!repository.existsByName(name)) {

            StockLocation location = new StockLocation();
            location.setName(name);
            location.setDescription(description);

            repository.save(location);
        }
    }
}