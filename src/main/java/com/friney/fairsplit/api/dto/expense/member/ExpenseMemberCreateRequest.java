package com.friney.fairsplit.api.dto.expense.member;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ExpenseMemberCreateRequest(
        @NotNull Long userId
) {
}