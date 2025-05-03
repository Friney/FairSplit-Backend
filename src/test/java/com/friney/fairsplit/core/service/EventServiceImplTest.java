package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.event.EventCreateDto;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.EventMapper;
import com.friney.fairsplit.core.repository.EventRepository;
import com.friney.fairsplit.core.service.event.EventServiceImpl;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void testGetAllByUserDetails() {
        Event event1 = Event.builder()
                .id(1L)
                .name("event 1")
                .build();

        Event event2 = Event.builder()
                .id(2L)
                .name("event 2")
                .build();

        List<Event> events = Arrays.asList(event1, event2);

        EventDto dto1 = EventDto.builder()
                .id(event1.getId())
                .name(event1.getName())
                .build();

        EventDto dto2 = EventDto.builder()
                .id(event2.getId())
                .name(event2.getName())
                .build();
        UserDetails userDetails = createTestUserDetails();
        RegisteredUser registeredUser = RegisteredUser.builder()
                .email(userDetails.getUsername())
                .build();

        when(userService.findByEmail(userDetails.getUsername())).thenReturn(registeredUser);
        when(eventRepository.findAllByOwner(registeredUser)).thenReturn(events);
        when(eventMapper.map(events)).thenReturn(Arrays.asList(dto1, dto2));

        List<EventDto> result = eventService.getAllByUserDetails(userDetails);

        assertEquals(Arrays.asList(dto1, dto2), result);
        verify(eventRepository, times(1)).findAllByOwner(registeredUser);
        verify(eventMapper, times(1)).map(events);
    }

    @Test
    void testGetAllByUserDetailsNoEvents() {
        UserDetails userDetails = createTestUserDetails();
        RegisteredUser registeredUser = RegisteredUser.builder()
                .email(userDetails.getUsername())
                .build();

        when(userService.findByEmail(userDetails.getUsername())).thenReturn(registeredUser);
        when(eventRepository.findAllByOwner(registeredUser)).thenReturn(List.of());

        List<EventDto> result = eventService.getAllByUserDetails(userDetails);

        assertTrue(result.isEmpty());
        verify(eventRepository, times(1)).findAllByOwner(registeredUser);
    }

    @Test
    void testGetDtoById() {
        Event event = Event.builder()
                .id(1L)
                .name("event")
                .build();

        EventDto dto = EventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.map(event)).thenReturn(dto);

        EventDto result = eventService.getDtoById(1L);

        assertEquals(dto, result);
        verify(eventRepository).findById(1L);
        verify(eventMapper, times(1)).map(event);
    }

    @Test
    void testGetDtoByIdNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> eventService.getDtoById(1L));

        assertEquals("event with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(eventRepository).findById(1L);
    }

    @Test
    void testGetById() {
        Event event = Event.builder()
                .id(1L)
                .name("event")
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event result = eventService.getById(1L);

        assertEquals(event, result);
        verify(eventRepository).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> eventService.getById(1L));

        assertEquals("event with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(eventRepository).findById(1L);
    }

    @Test
    void testCreate() {
        EventCreateDto createDto = EventCreateDto.builder()
                .name("event")
                .build();
        Event event = Event.builder()
                .id(1L)
                .name(createDto.name())
                .build();

        EventDto dto = EventDto.builder()
                .id(event.getId())
                .name(createDto.name())
                .build();
        UserDetails userDetails = createTestUserDetails();

        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.map(event)).thenReturn(dto);

        EventDto result = eventService.create(createDto, userDetails);

        assertEquals(dto, result);
        verify(eventRepository).save(any(Event.class));
        verify(eventMapper, times(1)).map(event);
    }

    private UserDetails createTestUserDetails() {
        return User.builder()
                .username("username")
                .password("password")
                .authorities(List.of())
                .build();
    }
}
