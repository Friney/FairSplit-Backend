package com.friney.fairsplit.core.service.event;

import com.friney.fairsplit.api.dto.event.EventCreateDto;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.core.entity.event.Event;

import java.util.List;

public interface EventService {

    List<EventDto> getAll();

    EventDto getDtoById(Long id);

    Event getById(Long id);

    EventDto create(EventCreateDto eventCreateDto);
}
