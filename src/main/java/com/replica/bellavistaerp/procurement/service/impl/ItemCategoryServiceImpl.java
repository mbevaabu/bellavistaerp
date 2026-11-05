package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.dto.ItemCategoryRequest;
import com.replica.bellavistaerp.procurement.dto.ItemCategoryResponse;
import com.replica.bellavistaerp.procurement.entity.ItemCategory;
import com.replica.bellavistaerp.procurement.entity.ItemDepartment;
import com.replica.bellavistaerp.procurement.repository.ItemCategoryRepository;
import com.replica.bellavistaerp.procurement.repository.ItemDepartmentRepository;
import com.replica.bellavistaerp.procurement.repository.ItemRepository;
import com.replica.bellavistaerp.procurement.service.ItemCategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemCategoryServiceImpl implements ItemCategoryService {

    private final ItemCategoryRepository categoryRepository;
    private final ItemDepartmentRepository departmentRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemCategoryResponse create(ItemCategoryRequest request) {

        ItemDepartment department = getDepartment(request.getDepartmentId());
        String name = request.getName().trim();

        if (categoryRepository.existsByNameAndDepartmentId(
                name, department.getId())) {
            throw new IllegalArgumentException(
                    "Category already exists in this department: " + name);
        }

        ItemCategory category = new ItemCategory();
        category.setName(name);
        category.setDescription(request.getDescription());
        category.setDepartment(department);
        category.setActive(true);

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCategoryResponse> findAll() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemCategoryResponse findById(Long id) {

        return mapToResponse(getCategory(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCategoryResponse> findByDepartment(Long departmentId) {

        getDepartment(departmentId);

        return categoryRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ItemCategoryResponse update(Long id, ItemCategoryRequest request) {

        ItemCategory category = getCategory(id);
        ItemDepartment department = getDepartment(request.getDepartmentId());

        String name = request.getName().trim();

        if (categoryRepository.existsByNameAndDepartmentIdAndIdNot(
                name, department.getId(), id)) {
            throw new IllegalArgumentException(
                    "Category already exists in this department: " + name);
        }

        category.setName(name);
        category.setDescription(request.getDescription());
        category.setDepartment(department);

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    public void deactivate(Long id) {

        ItemCategory category = getCategory(id);

        if (itemRepository.existsByCategoryIdAndActiveTrue(id)) {
            throw new IllegalStateException(
                    "Cannot deactivate this category because it contains active items.");
        }

        category.setActive(false);
        categoryRepository.save(category);
    }

    private ItemCategory getCategory(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found: " + id));
    }

    private ItemDepartment getDepartment(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Department not found: " + id));
    }

    private ItemCategoryResponse mapToResponse(ItemCategory category) {

        return ItemCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .departmentId(category.getDepartment().getId())
                .departmentName(category.getDepartment().getName())
                .active(category.isActive())
                .build();
    }
}