package com.friney.fairsplit.core.service.expense.member;

import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberDto;
import com.friney.fairsplit.core.entity.ExpenseMember.ExpenseMember;
import com.friney.fairsplit.core.exception.ServiceException;

import java.util.List;

public interface ExpenseMemberService {

    List<ExpenseMemberDto> getAllByExpenseId(Long expenseId);

    ExpenseMemberDto create(ExpenseMemberCreateDto expenseMemberCreateDto, Long expenseId);
}
