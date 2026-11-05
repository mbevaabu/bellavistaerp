package com.replica.bellavistaerp.procurement.config;

import com.replica.bellavistaerp.procurement.entity.ItemCategory;
import com.replica.bellavistaerp.procurement.entity.ItemDepartment;
import com.replica.bellavistaerp.procurement.repository.ItemCategoryRepository;
import com.replica.bellavistaerp.procurement.repository.ItemDepartmentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ItemCategoryInitializer {

    private final ItemCategoryRepository categoryRepository;
    private final ItemDepartmentRepository departmentRepository;

    @Bean
    CommandLineRunner initialiseItemCategories() {
        return args -> {

            // FOOD & KITCHEN
            createCategory("Food & Kitchen", "Greens",
                    "Fresh vegetables and leafy greens");
            createCategory("Food & Kitchen", "Dry Items",
                    "Dry food supplies");
            createCategory("Food & Kitchen", "Cereals",
                    "Grains and cereal products");
            createCategory("Food & Kitchen", "Meat & Poultry",
                    "Meat, chicken and poultry");
            createCategory("Food & Kitchen", "Dairy",
                    "Milk and dairy products");
            createCategory("Food & Kitchen", "Cooking Ingredients",
                    "Oil, spices and other cooking ingredients");

            // CLEANING
            createCategory("Cleaning", "Detergents",
                    "Washing and cleaning detergents");
            createCategory("Cleaning", "Disinfectants",
                    "Bleach and disinfectants");
            createCategory("Cleaning", "Cleaning Equipment",
                    "Mops, brooms and cleaning tools");

            // STATIONERY
            createCategory("Stationery", "Paper Products",
                    "Paper and exercise books");
            createCategory("Stationery", "Writing Materials",
                    "Pens, pencils and markers");
            createCategory("Stationery", "Teaching Materials",
                    "Teaching and classroom materials");

            // UNIFORMS
            createCategory("Uniforms", "Boys' Uniforms",
                    "Uniforms for boys");
            createCategory("Uniforms", "Girls' Uniforms",
                    "Uniforms for girls");
            createCategory("Uniforms", "Sportswear",
                    "Sports clothing and equipment");

            // MAINTENANCE
            createCategory("Maintenance", "Electrical",
                    "Electrical materials and fittings");
            createCategory("Maintenance", "Plumbing",
                    "Plumbing materials and fittings");
            createCategory("Maintenance", "Building Materials",
                    "General building and repair materials");
        };
    }

    private void createCategory(
            String departmentName,
            String categoryName,
            String description) {

        ItemDepartment department = departmentRepository
                .findByName(departmentName)
                .orElseThrow(() -> new IllegalStateException(
                        "Department not found: " + departmentName
                                + ". Ensure ItemDepartmentInitializer "
                                + "runs successfully before this initializer."
                ));

        boolean exists = categoryRepository
                .existsByNameAndDepartmentId(
                        categoryName,
                        department.getId()
                );

        if (!exists) {
            ItemCategory category = new ItemCategory();
            category.setName(categoryName);
            category.setDescription(description);
            category.setDepartment(department);
            category.setActive(true);

            categoryRepository.save(category);
        }
    }
}