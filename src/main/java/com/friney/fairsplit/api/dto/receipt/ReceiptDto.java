package com.friney.fairsplit.api.dto.receipt;

import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import java.util.List;
import lombok.Builder;

@Builder
public record ReceiptDto(
        Long id,
        String name,
        List<ExpenseDto> expenses,
        UserDto paidByUser
) {
}
