package com.friney.fairsplit.api.dto.Expense;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ExpenseCreateDto(
        String name,
        BigDecimal amount
) {
}
