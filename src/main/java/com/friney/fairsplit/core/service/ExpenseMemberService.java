package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberDto;
import com.friney.fairsplit.core.entity.ExpenseMember.ExpenseMember;
import com.friney.fairsplit.core.mapper.ExpenseMemberMapper;
import com.friney.fairsplit.core.repository.ExpenseMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseMemberService {
    private final ExpenseMemberRepository expenseMemberRepository;
    private final ExpenseService expenseService;
    private final UserService userService;
    private final ExpenseMemberMapper expenseMemberMapper;

    public List<ExpenseMemberDto> getAllByExpenseId(Long expenseId) {
        return expenseMemberRepository
                .findAll()
                .stream()
                .filter(
                        expenseMember -> expenseMember
                                .getExpense()
                                .getId()
                                .equals(expenseId)
                )
                .map(expenseMemberMapper::map)
                .toList();
    }

    public ExpenseMemberDto create(ExpenseMemberCreateDto expenseMemberCreateDto, Long expenseId) {
        ExpenseMember expenseMember = new ExpenseMember();
        expenseMember.setUser(userService.getById(expenseMemberCreateDto.userId()));
        expenseMember.setExpense(expenseService.getById(expenseId));
        expenseMember = expenseMemberRepository.save(expenseMember);
        return expenseMemberMapper.map(expenseMember);
    }
}
