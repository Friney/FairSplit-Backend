package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberDto;
import com.friney.fairsplit.core.service.expense.member.ExpenseMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Paths.EXPENSES_MEMBERS)
@RequiredArgsConstructor
public class ExpenseMemberController {

    private final ExpenseMemberService expenseMemberService;

    @GetMapping
    public List<ExpenseMemberDto> getAllByExpenseId(@PathVariable Long expenseId) {
        return expenseMemberService.getAllByExpenseId(expenseId);
    }

    @PostMapping
    public ExpenseMemberDto create(@RequestBody ExpenseMemberCreateDto expenseMemberCreateDto, @PathVariable Long expenseId) {
        return expenseMemberService.create(expenseMemberCreateDto, expenseId);
    }
}
