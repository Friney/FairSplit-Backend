package com.friney.fairsplit.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserCredentialsRequest(
        @NotBlank String email,
        @NotBlank String password
) {
}
