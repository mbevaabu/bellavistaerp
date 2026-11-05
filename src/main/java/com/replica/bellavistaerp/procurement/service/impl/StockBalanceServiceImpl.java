package com.replica.bellavistaerp.procurement.service.impl;

import com.replica.bellavistaerp.procurement.entity.StockBalance;
import com.replica.bellavistaerp.procurement.repository.StockBalanceRepository;
import com.replica.bellavistaerp.procurement.service.StockBalanceService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockBalanceServiceImpl implements StockBalanceService {

    private final StockBalanceRepository repository;

    @Override
    public List<StockBalance> findAll() {
        return repository.findAll();
    }

    @Override
    public StockBalance findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Stock balance not found: " + id));
    }

    @Override
    public StockBalance findByItemAndLocation(Long itemId, Long locationId) {
        return repository.findByItem_IdAndLocation_Id(itemId, locationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Stock balance not found for item "
                                        + itemId + " at location " + locationId
                        ));
    }

    @Override
    public List<StockBalance> findByItem(Long itemId) {
        return repository.findByItem_Id(itemId);
    }

    @Override
    public List<StockBalance> findByLocation(Long locationId) {
        return repository.findByLocation_Id(locationId);
    }
}