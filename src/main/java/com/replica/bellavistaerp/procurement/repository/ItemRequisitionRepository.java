
package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.ItemRequisition;
import com.replica.bellavistaerp.procurement.enums.RequisitionStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemRequisitionRepository
        extends JpaRepository<ItemRequisition, Long> {

    boolean existsByRequisitionNumber(String requisitionNumber);

    Optional<ItemRequisition> findByRequisitionNumber(
            String requisitionNumber);

    List<ItemRequisition> findByStatus(RequisitionStatus status);

    List<ItemRequisition> findByRequestDateBefore(
            LocalDateTime dateTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r FROM ItemRequisition r
            WHERE r.id = :requisitionId
            """)
    Optional<ItemRequisition> findByIdForUpdate(
            @Param("requisitionId") Long requisitionId);
}
