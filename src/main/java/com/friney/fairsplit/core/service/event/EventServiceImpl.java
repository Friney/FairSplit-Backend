package com.friney.fairsplit.core.service.event;

import com.friney.fairsplit.api.dto.event.EventCreateDto;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.event.EventUpdateDto;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.EventMapper;
import com.friney.fairsplit.core.repository.EventRepository;
import com.friney.fairsplit.core.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
        Sort sort = Sort.sort(Event.class).by(Event::getId).descending();
        RegisteredUser owner = userService.findByEmail(userDetails.getUsername());
        return eventMapper.map(eventRepository.findAllByOwner(owner, sort));
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

    @Override
    public EventDto update(EventUpdateDto eventUpdateDto, Long id, UserDetails userDetails) {
        Event event = getById(id);
        validateChangeRequest(event, userDetails);
        if (eventUpdateDto.name() != null) {
            event.setName(eventUpdateDto.name());
        }
        if (eventUpdateDto.description() != null) {
            event.setDescription(eventUpdateDto.description());
        }
        return eventMapper.map(eventRepository.save(event));
    }

    @Override
    public void delete(Long id, UserDetails userDetails) {
        Event event = getById(id);
        validateChangeRequest(event, userDetails);

        eventRepository.delete(event);
    }

    boolean hasPermissionOnChange(Event event, UserDetails userDetails) {
        return event.getOwner().getEmail().equals(userDetails.getUsername());
    }

    void validateChangeRequest(Event event, UserDetails userDetails) {
        if (!hasPermissionOnChange(event, userDetails)) {
            throw new ServiceException("you are not the owner of this event", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public boolean isExists(Long id) {
        return eventRepository.existsById(id);
    }
}