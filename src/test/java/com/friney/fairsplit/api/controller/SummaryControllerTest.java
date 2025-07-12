package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.summary.SummaryDto;
import com.friney.fairsplit.core.service.summary.SummaryService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        SummaryDto expectedSummary = SummaryDto.builder()
                .total(BigDecimal.valueOf(450))
                .build();

        when(summaryService.calculateSummary(1L)).thenReturn(expectedSummary);

        SummaryDto result = summaryController.calculateSummary(1L);

        assertEquals(expectedSummary, result);
        verify(summaryService, times(1)).calculateSummary(1L);
    }
}
