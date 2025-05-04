package com.friney.fairsplit.core.service.expense.member;

import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberUpdateDto;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface ExpenseMemberService {

    List<ExpenseMemberDto> getAllByExpenseId(Long expenseId);

    ExpenseMemberDto create(ExpenseMemberCreateDto expenseMemberCreateDto, Long expenseId);

    ExpenseMemberDto update(ExpenseMemberUpdateDto expenseMemberCreateDto, Long id, Long expenseId, UserDetails userDetails);

    ExpenseMember getById(Long id);

    void delete(Long id, Long expenseId, UserDetails userDetails);
}
