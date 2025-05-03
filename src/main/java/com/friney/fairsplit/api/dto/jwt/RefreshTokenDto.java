package com.friney.fairsplit.api.dto.jwt;

import lombok.Builder;

@Builder
public record RefreshTokenDto(
        String refreshToken
) {
}
