package com.friney.fairsplit.core.service.expense;

import com.friney.fairsplit.api.dto.Expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.Expense.ExpenseDto;
import com.friney.fairsplit.core.entity.Expense.Expense;

import java.util.List;

public interface ExpenseService {

    List<ExpenseDto> getAllByReceiptId(Long receiptId);

    ExpenseDto getDtoById(Long id);

    Expense getById(Long id);

    ExpenseDto create(ExpenseCreateDto expenseCreateDto, Long receiptId);
}
