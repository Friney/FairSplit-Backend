package com.friney.fairsplit.api.dto.jwt;

import lombok.Builder;

@Builder
public record JwtAuthenticationDto(
        String token,
        String refreshToken
) {
}
