package com.friney.fairsplit.core.service.summary;

import com.friney.fairsplit.core.entity.Summary.Summary;

public interface SummaryService {

    Summary calculateSummary(Long eventId);
}
