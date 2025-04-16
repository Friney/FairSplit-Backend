package com.friney.fairsplit.core.service.expense.member;

import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import com.friney.fairsplit.core.mapper.ExpenseMemberMapper;
import com.friney.fairsplit.core.repository.ExpenseMemberRepository;
import com.friney.fairsplit.core.service.expense.ExpenseService;
import com.friney.fairsplit.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseMemberServiceImpl implements ExpenseMemberService {

    private final ExpenseMemberRepository expenseMemberRepository;
    private final ExpenseService expenseService;
    private final UserService userService;
    private final ExpenseMemberMapper expenseMemberMapper;

    @Override
    public List<ExpenseMemberDto> getAllByExpenseId(Long expenseId) {
        ExpenseDto expense = expenseService.getDtoById(expenseId);
        return expense.expenseMembers();
    }

    @Override
    public ExpenseMemberDto create(ExpenseMemberCreateDto expenseMemberCreateDto, Long expenseId) {
        ExpenseMember expenseMember = ExpenseMember.builder()
                .user(userService.getById(expenseMemberCreateDto.userId()))
                .expense(expenseService.getById(expenseId))
                .build();
        return expenseMemberMapper.map(expenseMemberRepository.save(expenseMember));
    }
}
