package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberUpdateDto;
import com.friney.fairsplit.core.service.expense.member.ExpenseMemberService;
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
@RequestMapping(Paths.EXPENSES_MEMBERS)
@RequiredArgsConstructor
public class ExpenseMemberController {

    private final ExpenseMemberService expenseMemberService;

    @GetMapping
    public List<ExpenseMemberDto> getAllByExpenseId(@PathVariable Long expenseId) {
        return expenseMemberService.getAllByExpenseId(expenseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseMemberDto create(@RequestBody ExpenseMemberCreateDto expenseMemberCreateDto, @PathVariable Long expenseId) {
        return expenseMemberService.create(expenseMemberCreateDto, expenseId);
    }

    @PatchMapping("{id}")
    public ExpenseMemberDto update(@RequestBody ExpenseMemberUpdateDto expenseMemberUpdateDto, @PathVariable Long id,
                                   @PathVariable Long expenseId, @AuthenticationPrincipal UserDetails userDetails) {
        return expenseMemberService.update(expenseMemberUpdateDto, id, expenseId, userDetails);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @PathVariable Long expenseId, @AuthenticationPrincipal UserDetails userDetails) {
        expenseMemberService.delete(id, expenseId, userDetails);
    }
}
