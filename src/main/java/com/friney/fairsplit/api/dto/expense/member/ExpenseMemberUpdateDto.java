package com.friney.fairsplit.api.dto.expense.member;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ExpenseMemberUpdateDto(
        @Nullable Long userId
) {
}