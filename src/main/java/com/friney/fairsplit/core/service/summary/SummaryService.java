package com.friney.fairsplit.core.service.summary;

import com.friney.fairsplit.core.entity.summary.Summary;

public interface SummaryService {

    Summary calculateSummary(Long eventId);
}
