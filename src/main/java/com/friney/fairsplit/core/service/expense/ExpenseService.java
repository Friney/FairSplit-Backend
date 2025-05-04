package com.friney.fairsplit.core.service.expense;

import com.friney.fairsplit.api.dto.expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.ExpenseUpdateDto;
import com.friney.fairsplit.core.entity.expense.Expense;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface ExpenseService {

    List<ExpenseDto> getAllByReceiptId(Long receiptId);

    ExpenseDto getDtoById(Long id);

    Expense getById(Long id);

    ExpenseDto create(ExpenseCreateDto expenseCreateDto, Long receiptId);

    ExpenseDto update(ExpenseUpdateDto expenseCreateDto, Long id, Long receiptId, UserDetails userDetails);

    void delete(Long id, Long receiptId, UserDetails userDetails);
}
