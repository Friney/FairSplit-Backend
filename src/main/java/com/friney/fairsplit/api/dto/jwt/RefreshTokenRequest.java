package com.friney.fairsplit.api.dto.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequest(
        @NotBlank String refreshToken
) {
}
