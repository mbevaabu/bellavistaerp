package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.dto.ItemRequest;
import com.replica.bellavistaerp.procurement.dto.ItemResponse;
import com.replica.bellavistaerp.procurement.entity.Item;
import com.replica.bellavistaerp.procurement.entity.ItemCategory;
import com.replica.bellavistaerp.procurement.repository.ItemRepository;
import com.replica.bellavistaerp.procurement.repository.ItemCategoryRepository;
import com.replica.bellavistaerp.procurement.service.ItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemCategoryRepository categoryRepository;

    @Override
    public ItemResponse create(ItemRequest request) {

        validateStockLevels(request);

        String itemCode = request.getItemCode().trim();

        if (itemRepository.existsByItemCode(itemCode)) {
            throw new IllegalArgumentException(
                    "Item code already exists: " + itemCode);
        }

        ItemCategory category = getCategory(request.getCategoryId());

        Item item = new Item();
        item.setItemCode(itemCode);
        item.setName(request.getName().trim());
        item.setDescription(request.getDescription());
        item.setUnitOfMeasure(request.getUnitOfMeasure().trim());
        item.setCategory(category);
        item.setMinimumStockLevel(request.getMinimumStockLevel());
        item.setReorderLevel(request.getReorderLevel());
        item.setActive(true);

        return mapToResponse(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> findAll() {
        return itemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponse findById(Long id) {
        return mapToResponse(getItem(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> findByCategory(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> findByDepartment(Long departmentId) {
        return itemRepository.findByCategoryDepartmentId(departmentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ItemResponse update(Long id, ItemRequest request) {

        Item item = getItem(id);

        validateStockLevels(request);

        String newItemCode = request.getItemCode().trim();

        itemRepository.findByItemCode(newItemCode)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Item code already exists: " + newItemCode);
                });

        ItemCategory category = getCategory(request.getCategoryId());

        item.setItemCode(newItemCode);
        item.setName(request.getName().trim());
        item.setDescription(request.getDescription());
        item.setUnitOfMeasure(request.getUnitOfMeasure().trim());
        item.setCategory(category);
        item.setMinimumStockLevel(request.getMinimumStockLevel());
        item.setReorderLevel(request.getReorderLevel());

        return mapToResponse(itemRepository.save(item));
    }

    @Override
    public void deactivate(Long id) {

        Item item = getItem(id);

        if (!item.isActive()) {
            throw new IllegalArgumentException(
                    "Item is already deactivated: " + id);
        }

        item.setActive(false);

        itemRepository.save(item);
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Item not found: " + id));
    }

    private ItemCategory getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Item category not found: " + categoryId));
    }

    private void validateStockLevels(ItemRequest request) {

        if (request.getMinimumStockLevel() == null
                || request.getReorderLevel() == null) {
            throw new IllegalArgumentException(
                    "Minimum and reorder stock levels are required.");
        }

        if (request.getMinimumStockLevel().signum() < 0
                || request.getReorderLevel().signum() < 0) {
            throw new IllegalArgumentException(
                    "Stock levels cannot be negative.");
        }

        if (request.getReorderLevel()
                .compareTo(request.getMinimumStockLevel()) < 0) {
            throw new IllegalArgumentException(
                    "Reorder level cannot be lower than minimum stock level.");
        }
    }

    private ItemResponse mapToResponse(Item item) {

        return ItemResponse.builder()
                .id(item.getId())
                .itemCode(item.getItemCode())
                .name(item.getName())
                .description(item.getDescription())
                .unitOfMeasure(item.getUnitOfMeasure())
                .categoryId(item.getCategory().getId())
                .categoryName(item.getCategory().getName())
                .departmentName(item.getCategory().getDepartment().getName())
                .minimumStockLevel(item.getMinimumStockLevel())
                .reorderLevel(item.getReorderLevel())
                .active(item.isActive())
                .build();
    }
}