package com.friney.fairsplit.core.service.expense;

import com.friney.fairsplit.api.dto.expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ExpenseMapper;
import com.friney.fairsplit.core.repository.ExpenseRepository;
import com.friney.fairsplit.core.service.receipt.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ReceiptService receiptService;
    private final ExpenseMapper expenseMapper;

    @Override
    public List<ExpenseDto> getAllByReceiptId(Long receiptId) {
        ReceiptDto receipt = receiptService.getDtoById(receiptId);
        return receipt.expenses();
    }

    @Override
    public ExpenseDto getDtoById(Long id) {
        return expenseMapper.map(getById(id));
    }

    @Override
    public Expense getById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ServiceException("expense with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public ExpenseDto create(ExpenseCreateDto expenseCreateDto, Long receiptId) {
        Expense expense = Expense.builder()
                .name(expenseCreateDto.name())
                .amount(expenseCreateDto.amount())
                .receipt(receiptService.getById(receiptId))
                .build();
        return expenseMapper.map(expenseRepository.save(expense));
    }
}
