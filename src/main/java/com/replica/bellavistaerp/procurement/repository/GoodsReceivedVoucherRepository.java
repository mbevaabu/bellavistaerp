package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.GoodsReceivedVoucher;
import com.replica.bellavistaerp.procurement.enums.GoodsReceivedVoucherStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsReceivedVoucherRepository
        extends JpaRepository<GoodsReceivedVoucher, Long> {

    Optional<GoodsReceivedVoucher> findByGrvNumber(String grvNumber);

    List<GoodsReceivedVoucher> findByPurchaseOrder_Id(Long purchaseOrderId);

    List<GoodsReceivedVoucher> findByStatus(
            GoodsReceivedVoucherStatus status
    );

    List<GoodsReceivedVoucher> findByPurchaseOrder_IdOrderByReceivedDateDesc(
            Long purchaseOrderId
    );

    boolean existsByGrvNumber(String grvNumber);
}