package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.event.EventCreateDto;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.event.EventUpdateDto;
import com.friney.fairsplit.core.service.event.EventService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Paths.EVENTS)
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventDto> getAllByUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        return eventService.getAllByUserDetails(userDetails);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@RequestBody EventCreateDto eventCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        return eventService.create(eventCreateDto, userDetails);
    }

    @PatchMapping("{id}")
    public EventDto update(@RequestBody EventUpdateDto eventUpdateDto, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return eventService.update(eventUpdateDto, id, userDetails);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        eventService.delete(id, userDetails);
    }
}