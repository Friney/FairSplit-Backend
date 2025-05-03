package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.core.service.expense.ExpenseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    @Test
    void testGetAllByReceiptId() {
        ExpenseDto dto1 = ExpenseDto.builder()
                .id(1L)
                .name("expense 1")
                .build();

        ExpenseDto dto2 = ExpenseDto.builder()
                .id(2L)
                .name("expense 2")
                .build();

        List<ExpenseDto> expectedDtos = Arrays.asList(dto1, dto2);

        when(expenseService.getAllByReceiptId(1L)).thenReturn(expectedDtos);

        List<ExpenseDto> result = expenseController.getAllByReceiptId(1L);

        assertEquals(expectedDtos, result);
        verify(expenseService, times(1)).getAllByReceiptId(1L);
    }

    @Test
    void testCreate() {
        ExpenseCreateDto createDto = ExpenseCreateDto.builder()
                .name("Test expense")
                .amount(BigDecimal.valueOf(100))
                .build();

        ExpenseDto expectedDto = ExpenseDto.builder()
                .id(1L)
                .name(createDto.name())
                .build();

        when(expenseService.create(createDto, 1L)).thenReturn(expectedDto);

        ExpenseDto result = expenseController.create(createDto, 1L);

        assertEquals(expectedDto, result);
        verify(expenseService, times(1)).create(createDto, 1L);
    }
}
