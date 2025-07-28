package com.friney.fairsplit.core.service.expense;

import com.friney.fairsplit.api.dto.expense.ExpenseCreateRequest;
import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.ExpenseUpdateRequest;
import com.friney.fairsplit.core.entity.expense.Expense;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface ExpenseService {

    List<ExpenseDto> getAllByReceiptId(Long receiptId);

    ExpenseDto getDtoById(Long id);

    Expense getById(Long id);

    ExpenseDto create(ExpenseCreateRequest expenseCreateRequest, Long receiptId);

    ExpenseDto update(ExpenseUpdateRequest expenseCreateDto, Long id, Long receiptId, UserDetails userDetails);

    void delete(Long id, Long receiptId, UserDetails userDetails);

    boolean isExists(Long id);
}
