package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.ExpenseUpdateDto;
import com.friney.fairsplit.core.service.expense.ExpenseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseDto create(@RequestBody ExpenseCreateDto expenseCreateDto, @PathVariable Long receiptId) {
        return expenseService.create(expenseCreateDto, receiptId);
    }

    @PatchMapping("{id}")
    public ExpenseDto update(@RequestBody ExpenseUpdateDto expenseUpdateDto, @PathVariable Long id,
                             @PathVariable Long receiptId, @AuthenticationPrincipal UserDetails userDetails) {
        return expenseService.update(expenseUpdateDto, id, receiptId, userDetails);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @PathVariable Long receiptId, @AuthenticationPrincipal UserDetails userDetails) {
        expenseService.delete(id, receiptId, userDetails);
    }
}
