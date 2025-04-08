package com.friney.fairsplit.api.dto.expense;

import com.friney.fairsplit.api.dto.expense_member.ExpenseMemberDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ExpenseDto(
        Long id,
        String name,
        BigDecimal amount,
        List<ExpenseMemberDto> expenseMembers
) {
}
