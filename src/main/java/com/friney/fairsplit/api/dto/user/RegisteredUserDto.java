package com.friney.fairsplit.api.dto.user;

import lombok.Builder;

@Builder
public record RegisteredUserDto(
        Long id,
        String email,
        String name,
        String displayName
) {
}
