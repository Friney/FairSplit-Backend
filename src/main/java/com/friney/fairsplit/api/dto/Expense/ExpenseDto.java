package com.friney.fairsplit.api.dto.Expense;

import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberDto;
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
