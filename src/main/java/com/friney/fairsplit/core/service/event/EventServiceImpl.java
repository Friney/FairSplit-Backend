package com.friney.fairsplit.core.service.event;

import com.friney.fairsplit.api.dto.event.EventCreateDto;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.EventMapper;
import com.friney.fairsplit.core.repository.EventRepository;
import com.friney.fairsplit.core.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventMapper eventMapper;

    @Override
    public List<EventDto> getAllByUserDetails(UserDetails userDetails) {
        return eventMapper.map(eventRepository.findAllByOwner(userService.findByEmail(userDetails.getUsername())));
    }

    @Override
    public EventDto getDtoById(Long id) {
        return eventMapper.map(getById(id));
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ServiceException("event with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public EventDto create(EventCreateDto eventCreateDto, UserDetails userDetails) {
        Event event = Event.builder()
                .name(eventCreateDto.name())
                .description(eventCreateDto.description())
                .owner(userService.findByEmail(userDetails.getUsername()))
                .build();
        Event savedEvent = eventRepository.save(event);
        return eventMapper.map(savedEvent);
    }
}
