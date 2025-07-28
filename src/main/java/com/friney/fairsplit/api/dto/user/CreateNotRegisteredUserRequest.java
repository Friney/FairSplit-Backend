package com.friney.fairsplit.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateNotRegisteredUserRequest(
        @NotBlank String name
) {
}
