package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.Expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.Expense.ExpenseDto;
import com.friney.fairsplit.core.entity.Expense.Expense;
import com.friney.fairsplit.core.mapper.ExpenseMapper;
import com.friney.fairsplit.core.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ReceiptService receiptService;
    private final ExpenseMapper expenseMapper;

    public List<ExpenseDto> getAllByReceiptId(Long receiptId) {
        return expenseRepository
                .findAll()
                .stream()
                .filter(
                        expense -> expense
                                .getReceipt()
                                .getId()
                                .equals(receiptId)
                )
                .map(expenseMapper::map)
                .toList();
    }

    public Expense getById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public ExpenseDto create(ExpenseCreateDto expenseCreateDto, Long receiptId) {
        Expense expense = new Expense();
        expense.setName(expenseCreateDto.name());
        expense.setAmount(expenseCreateDto.amount());
        expense.setReceipt(receiptService.getById(receiptId));
        expense = expenseRepository.save(expense);
        return expenseMapper.map(expense);
    }
}
