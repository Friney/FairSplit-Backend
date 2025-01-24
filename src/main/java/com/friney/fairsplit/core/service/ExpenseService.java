package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.Expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.Expense.ExpenseDto;
import com.friney.fairsplit.api.dto.Receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.Expense.Expense;
import com.friney.fairsplit.core.entity.Receipt.Receipt;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ExpenseMapper;
import com.friney.fairsplit.core.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ReceiptService receiptService;
    private final ExpenseMapper expenseMapper;

    public List<ExpenseDto> getAllByReceiptId(Long receiptId) {
        ReceiptDto receipt = receiptService.getDtoById(receiptId);
        return receipt.expenses();
    }

    public ExpenseDto getDtoById(Long id) {
        return expenseMapper.map(getById(id));
    }

    public Expense getById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new ServiceException("Expense with id " + id + " not found", HttpStatus.NOT_FOUND));
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
