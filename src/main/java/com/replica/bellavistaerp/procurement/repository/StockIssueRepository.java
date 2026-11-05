package com.replica.bellavistaerp.procurement.repository;

import com.replica.bellavistaerp.procurement.entity.StockIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockIssueRepository extends JpaRepository<StockIssue, Long> {

    List<StockIssue> findByRequisition_Id(Long requisitionId);

    Optional<StockIssue> findByIssueNumber(String issueNumber);

    List<StockIssue> findByRequisition_IdOrderByIssueDateDesc(Long requisitionId);
}