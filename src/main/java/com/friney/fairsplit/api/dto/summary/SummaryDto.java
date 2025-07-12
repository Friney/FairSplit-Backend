package com.friney.fairsplit.api.dto.summary;

import com.friney.fairsplit.core.entity.summary.Debt;
import com.friney.fairsplit.core.entity.summary.PayerInfo;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record SummaryDto(
        BigDecimal total,
        List<Debt> debts,
        List<PayerInfo> payerInfos,
        List<ReceiptSummaryDto> receipts
) {
}
