package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptUpdateDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.entity.user.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ReceiptMapper;
import com.friney.fairsplit.core.repository.ReceiptRepository;
import com.friney.fairsplit.core.service.event.EventService;
import com.friney.fairsplit.core.service.receipt.ReceiptServiceImpl;
import com.friney.fairsplit.core.service.user.UserService;
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

        Receipt receipt1 = Receipt.builder()
                .id(1L)
                .name("receipt 1")
                .build();

        Receipt receipt2 = Receipt.builder()
                .id(2L)
                .name("receipt 2")
                .build();

        ReceiptDto dto1 = ReceiptDto.builder()
                .id(1L)
                .name(receipt1.getName())
                .paidByUser(user1)
                .build();

        ReceiptDto dto2 = ReceiptDto.builder()
                .id(2L)
                .name(receipt2.getName())
                .paidByUser(user2)
                .build();

        List<ReceiptDto> receiptsDto = Arrays.asList(dto1, dto2);
        List<Receipt> receipts = Arrays.asList(receipt1, receipt2);

        Sort sort = Sort.sort(Receipt.class).by(Receipt::getId).descending();

        when(eventService.isExists(1L)).thenReturn(true);
        when(receiptRepository.findAllByEventId(1L, sort)).thenReturn(receipts);
        when(receiptMapper.map(receipts)).thenReturn(receiptsDto);


        List<ReceiptDto> result = receiptService.getAllByEventId(1L);

        assertEquals(receiptsDto, result);
        verify(eventService, times(1)).isExists(1L);
        verify(receiptRepository, times(1)).findAllByEventId(1L, sort);
    }

    @Test
    void testGetAllByEventIdEventNotFound() {
        when(eventService.isExists(1L)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> receiptService.getAllByEventId(1L));

        assertEquals("event with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(eventService, times(1)).isExists(1L);
    }

    @Test
    void testGetDtoById() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .displayName("user")
                .build();

        Receipt receipt = Receipt.builder()
                .id(1L)
                .name("Test receipt")
                .paidByUser(user)
                .build();


        ReceiptDto expectedDto = ReceiptDto.builder()
                .id(receipt.getId())
                .name(receipt.getName())
                .paidByUser(userDto)
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
                .name("user")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .displayName("user")
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
                .paidByUser(userDto)
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
                .thenThrow(new ServiceException("controller with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> receiptService.create(createDto, 1L));

        assertEquals("controller with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(eventService, times(1)).getById(1L);
        verify(userService, times(1)).getById(1L);
    }

    @Test
    void testUpdate() {
        ReceiptUpdateDto updateDto = ReceiptUpdateDto.builder()
                .name("Updated receipt")
                .build();

        ReceiptDto expectedDto = ReceiptDto.builder()
                .name("receipt")
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

        when(receiptRepository.findById(1L)).thenReturn(Optional.of(recipient));
        when(receiptRepository.save(any(Receipt.class))).thenReturn(recipient);
        when(receiptMapper.map(recipient)).thenReturn(expectedDto);

        ReceiptDto result = receiptService.update(updateDto, 1L, 1L, createTestUserDetails());

        assertEquals(expectedDto, result);
        verify(receiptRepository, times(1)).findById(1L);
        verify(receiptRepository, times(1)).save(any(Receipt.class));
        verify(receiptMapper, times(1)).map((Receipt) any());
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

        when(receiptRepository.findById(1L)).thenReturn(Optional.of(recipient));
        doNothing().when(receiptRepository).delete(recipient);
        receiptService.delete(1L, 1L, createTestUserDetails());

        verify(receiptRepository, times(1)).findById(1L);
        verify(receiptRepository, times(1)).delete(recipient);
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

        UserDetails userDetails = createTestUserDetails();

        when(receiptRepository.findById(1L)).thenReturn(Optional.of(recipient));
        ServiceException exception = assertThrows(ServiceException.class, () -> receiptService.delete(1L, 1L, userDetails));
        assertEquals("you are not the owner of this receipt", exception.getMessage());
        verify(receiptRepository, times(1)).findById(1L);
    }


    private UserDetails createTestUserDetails() {
        return org.springframework.security.core.userdetails.User.builder()
                .username("username")
                .password("password")
                .authorities(List.of())
                .build();
    }
}
