package com.friney.fairsplit.api.dto.expense;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ExpenseUpdateDto(
        @Nullable String name,
        @Nullable BigDecimal amount
) {
}
