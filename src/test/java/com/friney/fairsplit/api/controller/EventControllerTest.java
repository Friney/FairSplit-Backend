package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.event.EventCreateDto;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.core.service.event.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

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

        when(eventService.getAll()).thenReturn(expectedDtos);
        List<EventDto> result = eventController.getAll();

        assertEquals(expectedDtos, result);
        verify(eventService, times(1)).getAll();
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

        when(eventService.create(createDto)).thenReturn(expectedDto);
        EventDto result = eventController.create(createDto);

        assertEquals(expectedDto, result);
        verify(eventService, times(1)).create(createDto);
    }
}
