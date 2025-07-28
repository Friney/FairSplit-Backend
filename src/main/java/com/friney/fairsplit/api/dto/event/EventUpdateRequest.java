package com.friney.fairsplit.api.dto.event;

import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record EventUpdateRequest(
        @Nullable String name,
        @Nullable String description
) {
}
