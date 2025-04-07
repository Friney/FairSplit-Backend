package com.friney.fairsplit.core.service.event;

import com.friney.fairsplit.api.dto.Event.EventCreateDto;
import com.friney.fairsplit.api.dto.Event.EventDto;
import com.friney.fairsplit.core.entity.Event.Event;

import java.util.List;

public interface EventService {

    List<EventDto> getAll();

    EventDto getDtoById(Long id);

    Event getById(Long id);

    EventDto create(EventCreateDto eventCreateDto);
}
