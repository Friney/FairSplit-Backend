package com.friney.fairsplit.api.controller.v1;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.summary.SummaryDto;
import com.friney.fairsplit.core.service.summary.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Paths.SUMMARY_V1)
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping
    public SummaryDto calculateSummary(@PathVariable Long eventId) {
        return summaryService.calculateSummary(eventId);
    }
}
