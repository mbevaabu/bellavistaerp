package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.entity.StockLocation;
import com.replica.bellavistaerp.procurement.repository.StockLocationRepository;
import com.replica.bellavistaerp.procurement.service.StockLocationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockLocationServiceImpl implements StockLocationService {

    private final StockLocationRepository repository;

    @Override
    public StockLocation create(StockLocation stockLocation) {

        String name = stockLocation.getName().trim();

        if (repository.existsByName(name)) {
            throw new IllegalArgumentException(
                    "Stock location already exists: " + name);
        }

        stockLocation.setName(name);
        stockLocation.setActive(true);

        return repository.save(stockLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockLocation> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public StockLocation findById(Long id) {
        return getStockLocation(id);
    }

    @Override
    public StockLocation update(Long id, StockLocation updatedLocation) {

        StockLocation location = getStockLocation(id);
        String newName = updatedLocation.getName().trim();

        repository.findByName(newName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Stock location name already exists: " + newName);
                });

        location.setName(newName);
        location.setDescription(updatedLocation.getDescription());

        return repository.save(location);
    }

    @Override
    public void deactivate(Long id) {

        StockLocation location = getStockLocation(id);
        location.setActive(false);

        repository.save(location);
    }

    private StockLocation getStockLocation(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stock location not found: " + id));
    }
}