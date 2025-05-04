package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberUpdateDto;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.entity.user.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ExpenseMemberMapper;
import com.friney.fairsplit.core.repository.ExpenseMemberRepository;
import com.friney.fairsplit.core.service.expense.ExpenseService;
import com.friney.fairsplit.core.service.expense.member.ExpenseMemberServiceImpl;
import com.friney.fairsplit.core.service.user.UserService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
                .name("user 1")
                .build();

        ExpenseMemberDto dto2 = ExpenseMemberDto.builder()
                .name("user 2")
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
                .thenThrow(new ServiceException("expense with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> expenseMemberService.getAllByExpenseId(1L));

        assertEquals("expense with id 1 not found", exception.getMessage());
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
                .name("user")
                .build();

        Expense expense = Expense.builder()
                .id(1L)
                .name("expense")
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
                .thenThrow(new ServiceException("user with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> expenseMemberService.create(createDto, 1L));

        assertEquals("user with id 1 not found", exception.getMessage());
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
                .name("user")
                .build();

        when(userService.getById(1L)).thenReturn(user);
        when(expenseService.getById(1L))
                .thenThrow(new ServiceException("expense with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> expenseMemberService.create(createDto, 1L));

        assertEquals("expense with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userService, times(1)).getById(1L);
        verify(expenseService, times(1)).getById(1L);
        verify(expenseMemberRepository, times(0)).save(any());
        verify(expenseMemberMapper, times(0)).map((ExpenseMember) any());
    }

    @Test
    void testUpdate() {
        ExpenseMemberUpdateDto updateDto = ExpenseMemberUpdateDto.builder()
                .userId(1L)
                .build();

        ExpenseMemberDto expectedDto = ExpenseMemberDto.builder()
                .name("username")
                .build();

        RegisteredUser user = RegisteredUser.builder()
                .email("username")
                .build();

        Event event = Event.builder()
                .id(1L)
                .owner(user)
                .build();

        Receipt recipient = Receipt.builder()
                .id(1L)
                .event(event)
                .build();

        Expense expense = Expense.builder()
                .id(1L)
                .receipt(recipient)
                .build();

        ExpenseMember expenseMember = ExpenseMember.builder()
                .id(1L)
                .expense(expense)
                .build();

        when(expenseMemberRepository.findById(1L)).thenReturn(Optional.of(expenseMember));
        when(expenseMemberRepository.save(any(ExpenseMember.class))).thenReturn(expenseMember);
        when(expenseMemberMapper.map(expenseMember)).thenReturn(expectedDto);

        ExpenseMemberDto result = expenseMemberService.update(updateDto, 1L, 1L, createTestUserDetails());

        assertEquals(expectedDto, result);
        verify(expenseMemberRepository, times(1)).findById(1L);
        verify(expenseMemberRepository, times(1)).save(any(ExpenseMember.class));
        verify(expenseMemberMapper, times(1)).map((ExpenseMember) any());
    }

    @Test
    void testDelete() {
        RegisteredUser user = RegisteredUser.builder()
                .email("username")
                .build();

        Event event = Event.builder()
                .id(1L)
                .owner(user)
                .build();

        Receipt recipient = Receipt.builder()
                .id(1L)
                .event(event)
                .build();

        Expense expense = Expense.builder()
                .id(1L)
                .receipt(recipient)
                .build();

        ExpenseMember expenseMember = ExpenseMember.builder()
                .id(1L)
                .expense(expense)
                .build();

        when(expenseMemberRepository.findById(1L)).thenReturn(Optional.of(expenseMember));
        doNothing().when(expenseMemberRepository).delete(expenseMember);
        expenseMemberService.delete(1L, 1L, createTestUserDetails());

        verify(expenseMemberRepository, times(1)).findById(1L);
        verify(expenseMemberRepository, times(1)).delete(expenseMember);
    }

    @Test
    void testDeleteWithoutOwner() {
        RegisteredUser user = RegisteredUser.builder()
                .email("wrong username")
                .build();

        Event event = Event.builder()
                .id(1L)
                .owner(user)
                .build();

        Receipt recipient = Receipt.builder()
                .id(1L)
                .event(event)
                .build();

        Expense expense = Expense.builder()
                .id(1L)
                .receipt(recipient)
                .build();

        ExpenseMember expenseMember = ExpenseMember.builder()
                .id(1L)
                .expense(expense)
                .build();

        UserDetails userDetails = createTestUserDetails();

        when(expenseMemberRepository.findById(1L)).thenReturn(Optional.of(expenseMember));
        ServiceException exception = assertThrows(ServiceException.class, () -> expenseMemberService.delete(1L, 1L, userDetails));
        assertEquals("you are not the owner of this expense member", exception.getMessage());
        verify(expenseMemberRepository, times(1)).findById(1L);
    }


    private UserDetails createTestUserDetails() {
        return org.springframework.security.core.userdetails.User.builder()
                .username("username")
                .password("password")
                .authorities(List.of())
                .build();
    }
}
