
package com.replica.bellavistaerp.procurement.service;

import com.replica.bellavistaerp.procurement.dto.StockIssueLineRequest;
import com.replica.bellavistaerp.procurement.entity.StockIssue;
import java.util.List;

public interface StockIssueService {

    StockIssue issueStock(
            Long requisitionId,
            String issuedBy,
            String receivedBy,
            String remarks,
            List<StockIssueLineRequest> issueLines
    );

    List<StockIssue> findAll();

    StockIssue findById(Long id);

    List<StockIssue> findByRequisition(Long requisitionId);

    StockIssue findByIssueNumber(String issueNumber);
}
