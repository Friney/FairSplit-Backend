package com.friney.fairsplit.api.dto.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ExpenseCreateRequest(
        @NotBlank String name,
        @NotNull BigDecimal amount
) {
}
