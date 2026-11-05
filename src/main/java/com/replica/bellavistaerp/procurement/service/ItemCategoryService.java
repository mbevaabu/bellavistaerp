package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.dto.ItemCategoryRequest;
import com.replica.bellavistaerp.procurement.dto.ItemCategoryResponse;

import java.util.List;

public interface ItemCategoryService {

    ItemCategoryResponse create(ItemCategoryRequest request);

    List<ItemCategoryResponse> findAll();

    ItemCategoryResponse findById(Long id);

    List<ItemCategoryResponse> findByDepartment(Long departmentId);

    ItemCategoryResponse update(Long id, ItemCategoryRequest request);

    void deactivate(Long id);
}