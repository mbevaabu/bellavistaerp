package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.entity.StockBalance;

import java.util.List;

public interface StockBalanceService {

    List<StockBalance> findAll();

    StockBalance findById(Long id);

    StockBalance findByItemAndLocation(
            Long itemId,
            Long locationId
    );

    List<StockBalance> findByItem(Long itemId);

    List<StockBalance> findByLocation(Long locationId);
}