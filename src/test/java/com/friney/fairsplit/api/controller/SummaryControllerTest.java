package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.core.entity.Summary.Summary;
import com.friney.fairsplit.core.service.summary.SummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryControllerTest {

    @Mock
    private SummaryService summaryService;

    @InjectMocks
    private SummaryController summaryController;

    @Test
    void testCalculateSummary() {
        Summary expectedSummary = Summary.builder()
                .total(BigDecimal.valueOf(450))
                .build();

        when(summaryService.calculateSummary(1L)).thenReturn(expectedSummary);

        Summary result = summaryController.calculateSummary(1L);

        assertEquals(expectedSummary, result);
        verify(summaryService, times(1)).calculateSummary(1L);
    }
}
