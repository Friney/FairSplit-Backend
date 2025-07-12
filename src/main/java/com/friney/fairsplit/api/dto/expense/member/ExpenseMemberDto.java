package com.friney.fairsplit.api.dto.expense.member;

import com.friney.fairsplit.api.dto.user.UserDto;
import lombok.Builder;

@Builder
public record ExpenseMemberDto(
        Long id,
        UserDto user
) {
}
