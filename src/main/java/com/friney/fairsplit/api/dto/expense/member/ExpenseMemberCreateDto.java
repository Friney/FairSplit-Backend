package com.friney.fairsplit.api.dto.expense.member;

import lombok.Builder;

@Builder
public record ExpenseMemberCreateDto(
        Long userId
) {
}