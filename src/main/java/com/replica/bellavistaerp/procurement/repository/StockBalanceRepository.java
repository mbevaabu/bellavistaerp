
package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.StockBalance;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockBalanceRepository
        extends JpaRepository<StockBalance, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s FROM StockBalance s
        WHERE s.item.id = :itemId
        AND s.location.id = :locationId
    """)
    Optional<StockBalance> findByItemAndLocationForUpdate(
            @Param("itemId") Long itemId,
            @Param("locationId") Long locationId
    );

    Optional<StockBalance> findByItem_IdAndLocation_Id(
            Long itemId,
            Long locationId
    );

    List<StockBalance> findByItem_Id(Long itemId);

    List<StockBalance> findByLocation_Id(Long locationId);
}
