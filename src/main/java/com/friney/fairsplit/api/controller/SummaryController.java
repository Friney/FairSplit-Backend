package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.core.entity.Summary.Summary;
import com.friney.fairsplit.core.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Paths.SUMMARY)
@RequiredArgsConstructor
public class SummaryController {
    private final SummaryService summaryService;

    @GetMapping
    public Summary calculateSummary(@PathVariable Long eventId) {
        return summaryService.calculateSummary(eventId);
    }
}
