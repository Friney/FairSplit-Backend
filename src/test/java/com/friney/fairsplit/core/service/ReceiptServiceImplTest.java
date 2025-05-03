package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.user.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ReceiptMapper;
import com.friney.fairsplit.core.repository.ReceiptRepository;
import com.friney.fairsplit.core.service.event.EventService;
import com.friney.fairsplit.core.service.receipt.ReceiptServiceImpl;
import com.friney.fairsplit.core.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceImplTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private ReceiptMapper receiptMapper;

    @InjectMocks
    private ReceiptServiceImpl receiptService;

    @Test
    void testGetAllByEventId() {
        ReceiptDto dto1 = ReceiptDto.builder()
                .id(1L)
                .name("receipt 1")
                .paidByUserName("user 1")
                .build();

        ReceiptDto dto2 = ReceiptDto.builder()
                .id(2L)
                .name("receipt 2")
                .paidByUserName("user 2")
                .build();

        List<ReceiptDto> expectedDtos = Arrays.asList(dto1, dto2);
        EventDto eventDto = EventDto.builder()
                .receipts(expectedDtos)
                .build();

        when(eventService.getDtoById(1L)).thenReturn(eventDto);

        List<ReceiptDto> result = receiptService.getAllByEventId(1L);

        assertEquals(expectedDtos, result);
        verify(eventService, times(1)).getDtoById(1L);
    }

    @Test
    void testGetAllByEventIdEventNotFound() {
        when(eventService.getDtoById(1L))
                .thenThrow(new ServiceException("event with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> receiptService.getAllByEventId(1L));

        assertEquals("event with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(eventService, times(1)).getDtoById(1L);
    }

    @Test
    void testGetDtoById() {
        Receipt receipt = Receipt.builder()
                .id(1L)
                .name("Test receipt")
                .paidByUser(User.builder()
                        .id(1L)
                        .name("Test user")
                        .build())
                .build();

        ReceiptDto expectedDto = ReceiptDto.builder()
                .id(receipt.getId())
                .name(receipt.getName())
                .paidByUserName(receipt.getPaidByUser().getName())
                .build();

        when(receiptRepository.findById(1L)).thenReturn(Optional.of(receipt));
        when(receiptMapper.map(receipt)).thenReturn(expectedDto);

        ReceiptDto result = receiptService.getDtoById(1L);

        assertEquals(expectedDto, result);
        verify(receiptRepository, times(1)).findById(1L);
        verify(receiptMapper, times(1)).map(receipt);
    }

    @Test
    void testGetDtoByIdNotFound() {
        when(receiptRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> receiptService.getDtoById(1L));

        assertEquals("receipt with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(receiptRepository, times(1)).findById(1L);
    }

    @Test
    void testCreate() {
        ReceiptCreateDto createDto = ReceiptCreateDto.builder()
                .name("Test receipt")
                .userId(1L)
                .build();

        Event event = Event.builder()
                .id(1L)
                .name("Test event")
                .build();

        User user = User.builder()
                .id(1L)
                .name("Test user")
                .build();

        Receipt savedReceipt = Receipt.builder()
                .id(1L)
                .name(createDto.name())
                .event(event)
                .paidByUser(user)
                .build();

        ReceiptDto expectedDto = ReceiptDto.builder()
                .id(savedReceipt.getId())
                .name(savedReceipt.getName())
                .paidByUserName(user.getName())
                .build();

        when(eventService.getById(1L)).thenReturn(event);
        when(userService.getById(1L)).thenReturn(user);
        when(receiptRepository.save(any(Receipt.class))).thenReturn(savedReceipt);
        when(receiptMapper.map(savedReceipt)).thenReturn(expectedDto);

        ReceiptDto result = receiptService.create(createDto, 1L);

        assertEquals(expectedDto, result);
        verify(eventService, times(1)).getById(1L);
        verify(userService, times(1)).getById(1L);
        verify(receiptRepository, times(1)).save(any(Receipt.class));
        verify(receiptMapper, times(1)).map(savedReceipt);
    }

    @Test
    void testCreateEventNotFound() {
        // Given
        ReceiptCreateDto createDto = ReceiptCreateDto.builder()
                .name("Test receipt")
                .userId(1L)
                .build();

        when(eventService.getById(1L))
                .thenThrow(new ServiceException("event with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> receiptService.create(createDto, 1L));

        assertEquals("event with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(eventService, times(1)).getById(1L);
    }

    @Test
    void testCreateUserNotFound() {
        ReceiptCreateDto createDto = ReceiptCreateDto.builder()
                .name("Test receipt")
                .userId(1L)
                .build();

        Event event = Event.builder()
                .id(1L)
                .name("Test event")
                .build();

        when(eventService.getById(1L)).thenReturn(event);
        when(userService.getById(1L))
                .thenThrow(new ServiceException("user with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> receiptService.create(createDto, 1L));

        assertEquals("user with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(eventService, times(1)).getById(1L);
        verify(userService, times(1)).getById(1L);
    }
}
