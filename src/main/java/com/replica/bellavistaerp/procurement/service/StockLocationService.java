package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.entity.StockLocation;

import java.util.List;

public interface StockLocationService {

    StockLocation create(StockLocation stockLocation);

    List<StockLocation> findAll();

    StockLocation findById(Long id);

    StockLocation update(Long id, StockLocation stockLocation);

    void deactivate(Long id);
}