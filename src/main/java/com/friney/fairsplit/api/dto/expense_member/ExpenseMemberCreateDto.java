package com.friney.fairsplit.api.dto.expense_member;

import lombok.Builder;

@Builder
public record ExpenseMemberCreateDto(
        Long userId) {
}