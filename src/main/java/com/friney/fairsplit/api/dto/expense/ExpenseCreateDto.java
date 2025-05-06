package com.friney.fairsplit.api.dto.expense;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ExpenseCreateDto(
        String name,
        BigDecimal amount
) {
}
