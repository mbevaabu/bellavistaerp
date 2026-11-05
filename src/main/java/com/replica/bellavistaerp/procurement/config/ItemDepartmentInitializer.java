package com.replica.bellavistaerp.procurement.config;

import com.replica.bellavistaerp.procurement.entity.ItemDepartment;
import com.replica.bellavistaerp.procurement.repository.ItemDepartmentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@RequiredArgsConstructor
public class ItemDepartmentInitializer {

    private final ItemDepartmentRepository repository;

    @Bean
    @Order(1)
    CommandLineRunner initialiseItemDepartments() {
        return args -> {

            createDepartment(
                    "Food & Kitchen",
                    "Food supplies and kitchen consumables"
            );

            createDepartment(
                    "Cleaning",
                    "Cleaning materials, detergents and disinfectants"
            );

            createDepartment(
                    "Stationery",
                    "Office, classroom and teaching stationery"
            );

            createDepartment(
                    "Uniforms",
                    "School uniforms and sportswear"
            );

            createDepartment(
                    "Maintenance",
                    "Building, electrical and plumbing materials"
            );
        };
    }

    private void createDepartment(String name, String description) {

        if (!repository.existsByName(name)) {

            ItemDepartment department = new ItemDepartment();

            department.setName(name);
            department.setDescription(description);
            department.setActive(true);

            repository.save(department);
        }
    }
}