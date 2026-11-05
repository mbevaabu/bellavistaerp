package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.dto.ItemDepartmentRequest;
import com.replica.bellavistaerp.procurement.dto.ItemDepartmentResponse;
import com.replica.bellavistaerp.procurement.entity.ItemDepartment;
import com.replica.bellavistaerp.procurement.repository.ItemDepartmentRepository;
import com.replica.bellavistaerp.procurement.service.ItemDepartmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemDepartmentServiceImpl implements ItemDepartmentService {

    private final ItemDepartmentRepository repository;

    @Override
    public ItemDepartmentResponse create(ItemDepartmentRequest request) {

        String name = request.getName().trim();

        if (repository.existsByName(name)) {
            throw new IllegalArgumentException(
                    "Department already exists: " + name);
        }

        ItemDepartment department = new ItemDepartment();
        department.setName(name);
        department.setDescription(request.getDescription());
        department.setActive(true);

        return mapToResponse(repository.save(department));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDepartmentResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDepartmentResponse findById(Long id) {
        return mapToResponse(getDepartment(id));
    }

    @Override
    public ItemDepartmentResponse update(
            Long id,
            ItemDepartmentRequest request) {

        ItemDepartment department = getDepartment(id);
        String newName = request.getName().trim();

        repository.findByName(newName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Department name already exists: " + newName);
                });

        department.setName(newName);
        department.setDescription(request.getDescription());

        return mapToResponse(repository.save(department));
    }

    @Override
    public void deactivate(Long id) {

        ItemDepartment department = getDepartment(id);

        department.setActive(false);

        repository.save(department);
    }

    private ItemDepartment getDepartment(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Department not found: " + id));
    }

    private ItemDepartmentResponse mapToResponse(
            ItemDepartment department) {

        return ItemDepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .active(department.isActive())
                .build();
    }
}