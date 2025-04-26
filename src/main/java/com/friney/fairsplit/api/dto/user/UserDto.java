package com.friney.fairsplit.api.dto.user;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String name,
        String displayName
) {
}
