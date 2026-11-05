package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.dto.ItemRequest;
import com.replica.bellavistaerp.procurement.dto.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse create(ItemRequest request);

    List<ItemResponse> findAll();

    ItemResponse findById(Long id);

    List<ItemResponse> findByCategory(Long categoryId);

    List<ItemResponse> findByDepartment(Long departmentId);

    ItemResponse update(Long id, ItemRequest request);

    void deactivate(Long id);
}