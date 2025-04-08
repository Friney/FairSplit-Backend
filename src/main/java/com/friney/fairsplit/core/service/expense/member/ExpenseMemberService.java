package com.friney.fairsplit.core.service.expense.member;

import com.friney.fairsplit.api.dto.expense_member.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.expense_member.ExpenseMemberDto;

import java.util.List;

public interface ExpenseMemberService {

    List<ExpenseMemberDto> getAllByExpenseId(Long expenseId);

    ExpenseMemberDto create(ExpenseMemberCreateDto expenseMemberCreateDto, Long expenseId);
}
