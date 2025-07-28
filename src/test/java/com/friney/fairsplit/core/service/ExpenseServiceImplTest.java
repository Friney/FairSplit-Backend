package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.expense.ExpenseCreateRequest;
import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.ExpenseUpdateRequest;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ExpenseMapper;
import com.friney.fairsplit.core.repository.ExpenseRepository;
import com.friney.fairsplit.core.service.expense.ExpenseServiceImpl;
import com.friney.fairsplit.core.service.receipt.ReceiptService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
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
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ReceiptService receiptService;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    void testGetAllByReceiptId() {
        Expense expense1 = Expense.builder()
                .id(1L)
                .name("expense 1")
                .amount(BigDecimal.valueOf(100))
                .build();

        Expense expense2 = Expense.builder()
                .id(2L)
                .name("expense 2")
                .amount(BigDecimal.valueOf(200))
                .build();

        ExpenseDto dto1 = ExpenseDto.builder()
                .id(expense1.getId())
                .name(expense1.getName())
                .amount(expense1.getAmount())
                .build();

        ExpenseDto dto2 = ExpenseDto.builder()
                .id(expense2.getId())
                .name(expense2.getName())
                .amount(expense2.getAmount())
                .build();

        List<ExpenseDto> expensesDto = Arrays.asList(dto1, dto2);
        List<Expense> expenses = Arrays.asList(expense1, expense2);

        Sort sort = Sort.sort(Expense.class).by(Expense::getId).descending();

        when(receiptService.isExists(1L)).thenReturn(true);
        when(expenseRepository.findAllByReceiptId(1L, sort)).thenReturn(expenses);
        when(expenseMapper.map(expenses)).thenReturn(expensesDto);

        List<ExpenseDto> result = expenseService.getAllByReceiptId(1L);

        assertEquals(expensesDto, result);
        verify(receiptService, times(1)).isExists(1L);
        verify(expenseRepository, times(1)).findAllByReceiptId(1L, sort);
    }

    @Test
    void testGetAllByReceiptIdNotFound() {
        when(receiptService.isExists(1L)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> expenseService.getAllByReceiptId(1L));

        assertEquals("receipt with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(receiptService, times(1)).isExists(1L);
    }

    @Test
    void testGetDtoById() {
        Expense expense = Expense.builder()
                .id(1L)
                .name("Test expense")
                .amount(BigDecimal.valueOf(100))
                .build();

        ExpenseDto expectedDto = ExpenseDto.builder()
                .id(expense.getId())
                .name(expense.getName())
                .amount(expense.getAmount())
                .build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseMapper.map(expense)).thenReturn(expectedDto);

        ExpenseDto result = expenseService.getDtoById(1L);

        assertEquals(expectedDto, result);
        verify(expenseRepository, times(1)).findById(1L);
        verify(expenseMapper, times(1)).map(expense);
    }

    @Test
    void testGetDtoByIdNotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> expenseService.getDtoById(1L));

        assertEquals("expense with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(expenseRepository, times(1)).findById(1L);
    }

    @Test
    void testCreate() {
        ExpenseCreateRequest createDto = ExpenseCreateRequest.builder()
                .name("Test expense")
                .amount(BigDecimal.valueOf(100))
                .build();

        Receipt receipt = Receipt.builder()
                .id(1L)
                .name("Test receipt")
                .build();

        Expense savedExpense = Expense.builder()
                .id(1L)
                .name(createDto.name())
                .amount(createDto.amount())
                .receipt(receipt)
                .build();

        ExpenseDto expectedDto = ExpenseDto.builder()
                .id(savedExpense.getId())
                .name(savedExpense.getName())
                .amount(savedExpense.getAmount())
                .build();

        when(receiptService.getById(1L)).thenReturn(receipt);
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);
        when(expenseMapper.map(savedExpense)).thenReturn(expectedDto);

        ExpenseDto result = expenseService.create(createDto, 1L);

        assertEquals(expectedDto, result);
        verify(receiptService, times(1)).getById(1L);
        verify(expenseRepository, times(1)).save(any(Expense.class));
        verify(expenseMapper, times(1)).map(savedExpense);
    }

    @Test
    void testCreateReceiptNotFound() {
        ExpenseCreateRequest createDto = ExpenseCreateRequest.builder()
                .name("Test expense")
                .amount(BigDecimal.valueOf(100))
                .build();

        when(receiptService.getById(1L))
                .thenThrow(new ServiceException("receipt with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> expenseService.create(createDto, 1L));

        assertEquals("receipt with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(receiptService, times(1)).getById(1L);
    }

    @Test
    void testUpdate() {
        ExpenseUpdateRequest updateDto = ExpenseUpdateRequest.builder()
                .name("Updated expense")
                .build();

        ExpenseDto expectedDto = ExpenseDto.builder()
                .name("expense")
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

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.map(expense)).thenReturn(expectedDto);

        ExpenseDto result = expenseService.update(updateDto, 1L, 1L, createTestUserDetails());

        assertEquals(expectedDto, result);
        verify(expenseRepository, times(1)).findById(1L);
        verify(expenseRepository, times(1)).save(any(Expense.class));
        verify(expenseMapper, times(1)).map((Expense) any());
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


        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        doNothing().when(expenseRepository).delete(expense);
        expenseService.delete(1L, 1L, createTestUserDetails());

        verify(expenseRepository, times(1)).findById(1L);
        verify(expenseRepository, times(1)).delete(expense);
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


        UserDetails userDetails = createTestUserDetails();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        ServiceException exception = assertThrows(ServiceException.class, () -> expenseService.delete(1L, 1L, userDetails));
        assertEquals("you are not the owner of this expense", exception.getMessage());
        verify(expenseRepository, times(1)).findById(1L);
    }


    private UserDetails createTestUserDetails() {
        return org.springframework.security.core.userdetails.User.builder()
                .username("username")
                .password("password")
                .authorities(List.of())
                .build();
    }
}
