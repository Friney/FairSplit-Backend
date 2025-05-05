package com.friney.fairsplit.core.service.expense;

import com.friney.fairsplit.api.dto.expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.ExpenseUpdateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ExpenseMapper;
import com.friney.fairsplit.core.repository.ExpenseRepository;
import com.friney.fairsplit.core.service.receipt.ReceiptService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

    @Override
    public ExpenseDto update(ExpenseUpdateDto expenseCreateDto, Long id, Long receiptId, UserDetails userDetails) {
        Expense expense = getById(id);
        validateChangeRequest(expense, receiptId, userDetails);

        if (expenseCreateDto.name() != null) {
            expense.setName(expenseCreateDto.name());
        }
        if (expenseCreateDto.amount() != null) {
            expense.setAmount(expenseCreateDto.amount());
        }
        return expenseMapper.map(expenseRepository.save(expense));
    }

    @Override
    public void delete(Long id, Long receiptId, UserDetails userDetails) {
        Expense expense = getById(id);
        validateChangeRequest(expense, receiptId, userDetails);

        expenseRepository.delete(expense);
    }

    boolean hasPermissionOnChange(Expense expense, UserDetails userDetails) {
        return expense.getReceipt().getEvent().getOwner().getEmail().equals(userDetails.getUsername());
    }

    void validateChangeRequest(Expense expense, Long receiptId, UserDetails userDetails) {
        if (!expense.getReceipt().getId().equals(receiptId)) {
            throw new ServiceException("expense with id " + expense.getId() + " in receipt with id " + receiptId + " not found", HttpStatus.NOT_FOUND);
        }
        if (!hasPermissionOnChange(expense, userDetails)) {
            throw new ServiceException("you are not the owner of this expense", HttpStatus.FORBIDDEN);
        }
    }
}
