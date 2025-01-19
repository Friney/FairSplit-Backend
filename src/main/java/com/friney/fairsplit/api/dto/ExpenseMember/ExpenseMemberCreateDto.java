package com.friney.fairsplit.api.dto.ExpenseMember;

import lombok.Builder;

@Builder
public record ExpenseMemberCreateDto(
        Long userId) {
}