package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.core.entity.summary.Summary;
import com.friney.fairsplit.core.service.summary.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
