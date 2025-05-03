package com.friney.fairsplit.api.dto.user;

import lombok.Builder;

@Builder
public record CreateRegisteredUserDto(
        String name,
        String email,
        String password,
        String confirmPassword
) {
}
