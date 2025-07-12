package com.friney.fairsplit.core.service.expense.member;

import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberUpdateDto;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ExpenseMemberMapper;
import com.friney.fairsplit.core.repository.ExpenseMemberRepository;
import com.friney.fairsplit.core.service.expense.ExpenseService;
import com.friney.fairsplit.core.service.user.UserService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
        List<ExpenseMemberDto> expenseMembers = expense.expenseMembers();
        expenseMembers.sort(Comparator.comparing(ExpenseMemberDto::id));
        return expenseMembers;
    }

    @Override
    public ExpenseMemberDto create(ExpenseMemberCreateDto expenseMemberCreateDto, Long expenseId) {
        ExpenseMember expenseMember = ExpenseMember.builder()
                .user(userService.getById(expenseMemberCreateDto.userId()))
                .expense(expenseService.getById(expenseId))
                .build();
        return expenseMemberMapper.map(expenseMemberRepository.save(expenseMember));
    }

    @Override
    public ExpenseMember getById(Long id) {
        return expenseMemberRepository.findById(id)
                .orElseThrow(() -> new ServiceException("expense member with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public ExpenseMemberDto update(ExpenseMemberUpdateDto expenseMemberCreateDto, Long id, Long expenseId, UserDetails userDetails) {
        ExpenseMember expenseMember = getById(id);
        validateChangeRequest(expenseMember, expenseId, userDetails);

        if (expenseMemberCreateDto.userId() != null) {
            expenseMember.setUser(userService.getById(expenseMemberCreateDto.userId()));
        }
        return expenseMemberMapper.map(expenseMemberRepository.save(expenseMember));
    }

    @Override
    public void delete(Long id, Long expenseId, UserDetails userDetails) {
        ExpenseMember expenseMember = getById(id);
        validateChangeRequest(expenseMember, expenseId, userDetails);

        expenseMemberRepository.delete(expenseMember);
    }

    boolean hasPermissionOnChange(ExpenseMember expenseMember, UserDetails userDetails) {
        return expenseMember.getExpense().getReceipt().getEvent().getOwner().getEmail().equals(userDetails.getUsername());
    }

    void validateChangeRequest(ExpenseMember expenseMember, Long expenseId, UserDetails userDetails) {
        if (!expenseMember.getExpense().getId().equals(expenseId)) {
            throw new ServiceException("expense member with id " + expenseMember.getId() + " in expense with id " + expenseId + " not found", HttpStatus.NOT_FOUND);
        }
        if (!hasPermissionOnChange(expenseMember, userDetails)) {
            throw new ServiceException("you are not the owner of this expense member", HttpStatus.FORBIDDEN);
        }
    }
}
