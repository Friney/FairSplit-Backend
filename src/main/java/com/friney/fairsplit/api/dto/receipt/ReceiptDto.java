package com.friney.fairsplit.api.dto.receipt;

import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import lombok.Builder;

import java.util.List;

@Builder
public record ReceiptDto(
        Long id,
        String name,
        List<ExpenseDto> expenses,
        String paidByUserName
) {
}
