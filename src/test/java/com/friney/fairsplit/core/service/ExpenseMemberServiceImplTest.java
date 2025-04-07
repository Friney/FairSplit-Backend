package com.friney.fairsplit.core.service;

import com.friney.fairsplit.core.service.expense.ExpenseService;
import com.friney.fairsplit.core.service.expense.member.ExpenseMemberServiceImpl;
import com.friney.fairsplit.core.service.user.UserService;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.friney.fairsplit.api.dto.Expense.ExpenseDto;
import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberDto;
import com.friney.fairsplit.core.entity.Expense.Expense;
import com.friney.fairsplit.core.entity.ExpenseMember.ExpenseMember;
import com.friney.fairsplit.core.entity.User.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ExpenseMemberMapper;
import com.friney.fairsplit.core.repository.ExpenseMemberRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseMemberServiceImplTest {

    @Mock
    private ExpenseMemberRepository expenseMemberRepository;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private UserService userService;

    @Mock
    private ExpenseMemberMapper expenseMemberMapper;

    @InjectMocks
    private ExpenseMemberServiceImpl expenseMemberService;

    @Test
    void testGetAllByExpenseId() {
        ExpenseMemberDto dto1 = ExpenseMemberDto.builder()
                .name("User 1")
                .build();

        ExpenseMemberDto dto2 = ExpenseMemberDto.builder()
                .name("User 2")
                .build();

        List<ExpenseMemberDto> expectedDtos = Arrays.asList(dto1, dto2);
        ExpenseDto expenseDto = ExpenseDto.builder()
                .expenseMembers(expectedDtos)
                .build();

        when(expenseService.getDtoById(1L)).thenReturn(expenseDto);
        List<ExpenseMemberDto> result = expenseMemberService.getAllByExpenseId(1L);
        assertEquals(expectedDtos, result);
        verify(expenseService, times(1)).getDtoById(1L);
    }

    @Test
    void testGetAllByExpenseIdNotFound() {
        when(expenseService.getDtoById(1L))
                .thenThrow(new ServiceException("Expense with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> expenseMemberService.getAllByExpenseId(1L));

        assertEquals("Expense with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(expenseService, times(1)).getDtoById(1L);
    }

    @Test
    void testCreate() {
        ExpenseMemberCreateDto createDto = ExpenseMemberCreateDto.builder()
                .userId(1L)
                .build();

        User user = User.builder()
                .id(1L)
                .name("User")
                .build();

        Expense expense = Expense.builder()
                .id(1L)
                .name("Expense")
                .build();

        ExpenseMember savedExpenseMember = ExpenseMember.builder()
                .id(1L)
                .user(user)
                .expense(expense)
                .build();

        ExpenseMemberDto expectedDto = ExpenseMemberDto.builder()
                .name(user.getName())
                .build();

        when(userService.getById(1L)).thenReturn(user);
        when(expenseService.getById(1L)).thenReturn(expense);
        when(expenseMemberRepository.save(any(ExpenseMember.class))).thenReturn(savedExpenseMember);
        when(expenseMemberMapper.map(savedExpenseMember)).thenReturn(expectedDto);

        ExpenseMemberDto result = expenseMemberService.create(createDto, 1L);

        assertEquals(expectedDto, result);
        verify(userService, times(1)).getById(1L);
        verify(expenseService, times(1)).getById(1L);
        verify(expenseMemberRepository, times(1)).save(any(ExpenseMember.class));
        verify(expenseMemberMapper, times(1)).map((ExpenseMember) any());
    }

    @Test
    void testCreateUserNotFound() {
        ExpenseMemberCreateDto createDto = ExpenseMemberCreateDto.builder()
                .userId(1L)
                .build();

        when(userService.getById(1L))
                .thenThrow(new ServiceException("User with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> expenseMemberService.create(createDto, 1L));

        assertEquals("User with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userService, times(1)).getById(1L);
        verify(expenseService, times(0)).getById(any());
        verify(expenseMemberRepository, times(0)).save(any());
        verify(expenseMemberMapper, times(0)).map((ExpenseMember) any());
    }

    @Test
    void testCreateExpenseNotFound() {
        ExpenseMemberCreateDto createDto = ExpenseMemberCreateDto.builder()
                .userId(1L)
                .build();

        User user = User.builder()
                .id(1L)
                .name("User")
                .build();

        when(userService.getById(1L)).thenReturn(user);
        when(expenseService.getById(1L))
                .thenThrow(new ServiceException("Expense with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> expenseMemberService.create(createDto, 1L));

        assertEquals("Expense with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userService, times(1)).getById(1L);
        verify(expenseService, times(1)).getById(1L);
        verify(expenseMemberRepository, times(0)).save(any());
        verify(expenseMemberMapper, times(0)).map((ExpenseMember) any());
    }
}
