package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberCreateRequest;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.service.expense.member.ExpenseMemberService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseMemberControllerTest {

    @Mock
    private ExpenseMemberService expenseMemberService;

    @InjectMocks
    private ExpenseMemberController expenseMemberController;

    @Test
    void testGetAllByExpenseId() {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("user 1")
                .displayName("user 1")
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("user 2")
                .displayName("user 2")
                .build();

        ExpenseMemberDto dto1 = ExpenseMemberDto.builder()
                .user(user1)
                .build();

        ExpenseMemberDto dto2 = ExpenseMemberDto.builder()
                .user(user2)
                .build();

        List<ExpenseMemberDto> expectedDtos = Arrays.asList(dto1, dto2);

        when(expenseMemberService.getAllByExpenseId(1L)).thenReturn(expectedDtos);

        List<ExpenseMemberDto> result = expenseMemberController.getAllByExpenseId(1L);

        assertEquals(expectedDtos, result);
        verify(expenseMemberService, times(1)).getAllByExpenseId(1L);
    }

    @Test
    void testCreate() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("user")
                .displayName("user")
                .build();

        ExpenseMemberCreateRequest createDto = ExpenseMemberCreateRequest.builder()
                .userId(1L)
                .build();

        ExpenseMemberDto expectedDto = ExpenseMemberDto.builder()
                .user(user)
                .build();

        when(expenseMemberService.create(createDto, 1L)).thenReturn(expectedDto);

        ExpenseMemberDto result = expenseMemberController.create(createDto, 1L);

        assertEquals(expectedDto, result);
        verify(expenseMemberService, times(1)).create(createDto, 1L);
    }
}
