package com.friney.fairsplit.api.dto.event;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EventCreateRequest(
        @NotBlank String name,
        @NotBlank String description
) {
}
