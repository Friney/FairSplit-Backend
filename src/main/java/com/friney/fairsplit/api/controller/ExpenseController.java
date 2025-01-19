package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.Expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.Expense.ExpenseDto;
import com.friney.fairsplit.core.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Paths.EXPENSES)
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping
    public List<ExpenseDto> getAllByReceiptId(@PathVariable Long receiptId) {
        return expenseService.getAllByReceiptId(receiptId);
    }

    @PostMapping
    public ExpenseDto create(@RequestBody ExpenseCreateDto expenseCreateDto, @PathVariable Long receiptId) {
        return expenseService.create(expenseCreateDto, receiptId);
    }
}
