package com.friney.fairsplit.core.service.summary;

import com.friney.fairsplit.api.dto.summary.SummaryDto;

public interface SummaryService {

    SummaryDto calculateSummary(Long eventId);
}
