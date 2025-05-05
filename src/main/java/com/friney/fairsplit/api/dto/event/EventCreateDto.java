package com.friney.fairsplit.api.dto.event;

import lombok.Builder;

@Builder
public record EventCreateDto(
        String name,
        String description
) {
}
