package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.Event.EventCreateDto;
import com.friney.fairsplit.api.dto.Event.EventDto;
import com.friney.fairsplit.core.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Paths.EVENTS)
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventDto> getAll() {
        return eventService.getAll();
    }

    @PostMapping
    public EventDto create(@RequestBody EventCreateDto eventCreateDto) {
        return eventService.create(eventCreateDto);
    }
}
