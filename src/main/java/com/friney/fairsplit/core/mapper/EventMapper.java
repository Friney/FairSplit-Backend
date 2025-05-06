package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.core.entity.event.Event;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ReceiptMapper.class)
public interface EventMapper {
    EventDto map(Event event);

    List<EventDto> map(List<Event> events);
}
