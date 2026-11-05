package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.GoodsReceivedVoucherLine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsReceivedVoucherLineRepository
        extends JpaRepository<GoodsReceivedVoucherLine, Long> {

    List<GoodsReceivedVoucherLine> findByGoodsReceivedVoucher_Id(
            Long grvId
    );

    List<GoodsReceivedVoucherLine> findByPurchaseOrderLine_Id(
            Long purchaseOrderLineId
    );

    List<GoodsReceivedVoucherLine> findByItem_Id(
            Long itemId
    );
}