package com.friney.fairsplit.core.service.event;

import com.friney.fairsplit.api.dto.Event.EventCreateDto;
import com.friney.fairsplit.api.dto.Event.EventDto;
import com.friney.fairsplit.core.entity.Event.Event;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.EventMapper;
import com.friney.fairsplit.core.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventDto> getAll() {
        return eventMapper.map(eventRepository.findAll());
    }

    @Override
    public EventDto getDtoById(Long id) {
        return eventMapper.map(getById(id));
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Event with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public EventDto create(EventCreateDto eventCreateDto) {
        Event event = new Event();
        event.setName(eventCreateDto.name());
        event.setDescription(eventCreateDto.description());
        Event savedEvent = eventRepository.save(event);
        return eventMapper.map(savedEvent);
    }
}
