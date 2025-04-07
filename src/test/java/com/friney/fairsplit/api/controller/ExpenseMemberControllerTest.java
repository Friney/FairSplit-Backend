package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.core.service.expense.member.ExpenseMemberService;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.ExpenseMember.ExpenseMemberDto;

@ExtendWith(MockitoExtension.class)
class ExpenseMemberControllerTest {

    @Mock
    private ExpenseMemberService expenseMemberService;

    @InjectMocks
    private ExpenseMemberController expenseMemberController;

    @Test
    void testGetAllByExpenseId() {
        ExpenseMemberDto dto1 = ExpenseMemberDto.builder()
                .name("User 1")
                .build();

        ExpenseMemberDto dto2 = ExpenseMemberDto.builder()
                .name("User 2")
                .build();

        List<ExpenseMemberDto> expectedDtos = Arrays.asList(dto1, dto2);

        when(expenseMemberService.getAllByExpenseId(1L)).thenReturn(expectedDtos);

        List<ExpenseMemberDto> result = expenseMemberController.getAllByExpenseId(1L);

        assertEquals(expectedDtos, result);
        verify(expenseMemberService, times(1)).getAllByExpenseId(1L);
    }

    @Test
    void testCreate() {
        ExpenseMemberCreateDto createDto = ExpenseMemberCreateDto.builder()
                .userId(1L)
                .build();

        ExpenseMemberDto expectedDto = ExpenseMemberDto.builder()
                .name("Test User")
                .build();

        when(expenseMemberService.create(createDto, 1L)).thenReturn(expectedDto);

        ExpenseMemberDto result = expenseMemberController.create(createDto, 1L);

        assertEquals(expectedDto, result);
        verify(expenseMemberService, times(1)).create(createDto, 1L);
    }
}
