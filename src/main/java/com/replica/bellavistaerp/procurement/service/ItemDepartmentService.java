package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.dto.ItemDepartmentRequest;
import com.replica.bellavistaerp.procurement.dto.ItemDepartmentResponse;

import java.util.List;

public interface ItemDepartmentService {

    ItemDepartmentResponse create(ItemDepartmentRequest request);

    List<ItemDepartmentResponse> findAll();

    ItemDepartmentResponse findById(Long id);

    ItemDepartmentResponse update(Long id, ItemDepartmentRequest request);

    void deactivate(Long id);
}