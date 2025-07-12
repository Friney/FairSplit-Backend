package com.friney.fairsplit.api.dto.summary;

import com.friney.fairsplit.core.entity.summary.PayerInfo;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record ReceiptSummaryDto(
        String name,
        BigDecimal total,
        List<PayerInfo> payerInfos,
        List<ExpenseSummaryDto> expenses
) {
}

