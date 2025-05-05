package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.event.EventCreateDto;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.event.EventUpdateDto;
import com.friney.fairsplit.core.service.event.EventService;
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
class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Test
    void testGetAll() {
        EventDto dto1 = EventDto.builder()
                .id(1L)
                .name("event 1")
                .build();

        EventDto dto2 = EventDto.builder()
                .id(2L)
                .name("event 2")
                .build();

        List<EventDto> expectedDtos = Arrays.asList(dto1, dto2);
        UserDetails userDetails = createTestUserDetails();

        when(eventService.getAllByUserDetails(userDetails)).thenReturn(expectedDtos);
        List<EventDto> result = eventController.getAllByUserDetails(userDetails);

        assertEquals(expectedDtos, result);
        verify(eventService, times(1)).getAllByUserDetails(userDetails);
    }

    @Test
    void testCreate() {
        EventCreateDto createDto = EventCreateDto.builder()
                .name("New event")
                .build();

        EventDto expectedDto = EventDto.builder()
                .id(1L)
                .name(createDto.name())
                .build();

        UserDetails userDetails = createTestUserDetails();
        when(eventService.create(createDto, userDetails)).thenReturn(expectedDto);
        EventDto result = eventController.create(createDto, userDetails);

        assertEquals(expectedDto, result);
        verify(eventService, times(1)).create(createDto, userDetails);
    }

    @Test
    void testUpdate() {
        EventUpdateDto updateDto = EventUpdateDto.builder()
                .name("Updated event")
                .build();

        EventDto expectedDto = EventDto.builder()
                .id(1L)
                .name(updateDto.name())
                .build();

        UserDetails userDetails = createTestUserDetails();
        when(eventService.update(updateDto, 1L, userDetails)).thenReturn(expectedDto);
        EventDto result = eventController.update(updateDto, 1L, userDetails);

        assertEquals(expectedDto, result);
        verify(eventService, times(1)).update(updateDto, 1L, userDetails);
    }

    @Test
    void testDelete() {
        UserDetails userDetails = createTestUserDetails();
        long eventId = 1L;
        eventController.delete(eventId, userDetails);
        verify(eventService, times(1)).delete(eventId, userDetails);
    }

    private UserDetails createTestUserDetails() {
        return User.builder()
                .username("username")
                .password("password")
                .authorities(List.of())
                .build();
    }
}
