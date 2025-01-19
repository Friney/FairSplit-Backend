package com.friney.fairsplit.api.dto.Receipt;

import com.friney.fairsplit.api.dto.Expense.ExpenseDto;
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