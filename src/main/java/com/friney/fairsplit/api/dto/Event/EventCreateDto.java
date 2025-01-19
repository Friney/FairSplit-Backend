package com.friney.fairsplit.api.dto.Event;

import lombok.Builder;

@Builder
public record EventCreateDto(
        String name,
        String description) {
}
