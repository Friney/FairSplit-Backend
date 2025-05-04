package com.friney.fairsplit.core.service.event;

import com.friney.fairsplit.api.dto.event.EventCreateDto;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.event.EventUpdateDto;
import com.friney.fairsplit.core.entity.event.Event;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface EventService {

    List<EventDto> getAllByUserDetails(UserDetails userDetails);

    EventDto getDtoById(Long id);

    Event getById(Long id);

    EventDto create(EventCreateDto eventCreateDto, UserDetails userDetails);

    EventDto update(EventUpdateDto eventUpdateDto, Long id, UserDetails userDetails);

    void delete(Long id, UserDetails userDetails);
}
