package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.receipt.ReceiptCreateRequest;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptUpdateRequest;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.service.receipt.ReceiptService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptControllerTest {

    @Mock
    private ReceiptService receiptService;

    @InjectMocks
    private ReceiptController receiptController;

    @Test
    void testGetAllByEventId() {
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

        ReceiptDto dto1 = ReceiptDto.builder()
                .id(1L)
                .name("receipt 1")
                .paidByUser(user1)
                .build();

        ReceiptDto dto2 = ReceiptDto.builder()
                .id(2L)
                .name("receipt 2")
                .paidByUser(user2)
                .build();

        List<ReceiptDto> expectedDtos = Arrays.asList(dto1, dto2);

        when(receiptService.getAllByEventId(1L)).thenReturn(expectedDtos);

        List<ReceiptDto> result = receiptController.getAllByEventId(1L);

        assertEquals(expectedDtos, result);
        verify(receiptService, times(1)).getAllByEventId(1L);
    }

    @Test
    void testCreateReceipt() {
        ReceiptCreateRequest createDto = ReceiptCreateRequest.builder()
                .name("Test receipt")
                .userId(1L)
                .build();

        UserDto user = UserDto.builder()
                .id(1L)
                .name("user")
                .displayName("user")
                .build();

        ReceiptDto expectedDto = ReceiptDto.builder()
                .id(1L)
                .name(createDto.name())
                .paidByUser(user)
                .build();

        when(receiptService.create(createDto, 1L)).thenReturn(expectedDto);

        ReceiptDto result = receiptController.createReceipt(createDto, 1L);

        assertEquals(expectedDto, result);
        verify(receiptService, times(1)).create(createDto, 1L);
    }

    @Test
    void testUpdateReceipt() {
        ReceiptUpdateRequest updateDto = ReceiptUpdateRequest.builder()
                .name("Updated receipt")
                .build();

        UserDto user = UserDto.builder()
                .id(1L)
                .name("user")
                .displayName("user")
                .build();

        ReceiptDto expectedDto = ReceiptDto.builder()
                .id(1L)
                .name(updateDto.name())
                .paidByUser(user)
                .build();

        UserDetails userDetails = createTestUserDetails();
        when(receiptService.update(updateDto, 1L, 1L, userDetails)).thenReturn(expectedDto);

        ReceiptDto result = receiptController.update(updateDto, 1L, 1L, userDetails);

        assertEquals(expectedDto, result);
        verify(receiptService, times(1)).update(updateDto, 1L, 1L, userDetails);
    }

    @Test
    void testDeleteReceipt() {
        UserDetails userDetails = createTestUserDetails();
        receiptController.delete(1L, 1L, userDetails);
        verify(receiptService, times(1)).delete(1L, 1L, userDetails);
    }

    private UserDetails createTestUserDetails() {
        return User.builder()
                .username("username")
                .password("password")
                .authorities(List.of())
                .build();
    }
}
