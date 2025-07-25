package com.friney.fairsplit.core.entity.summary;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Summary {
    private BigDecimal total;
    private List<Debt> debts;
    private List<PayerInfo> payerInfos;
    private List<ReceiptSummary> receipts;
}
