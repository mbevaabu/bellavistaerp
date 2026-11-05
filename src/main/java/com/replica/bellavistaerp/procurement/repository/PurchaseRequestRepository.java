package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.PurchaseRequest;
import com.replica.bellavistaerp.procurement.enums.PurchaseRequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {

    Optional<PurchaseRequest> findByRequestNumber(String requestNumber);

    Optional<PurchaseRequest> findByRequisition_Id(Long requisitionId);

    List<PurchaseRequest> findByStatus(PurchaseRequestStatus status);

    boolean existsByRequisition_Id(Long requisitionId);
}