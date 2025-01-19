package com.friney.fairsplit.api.dto.User;

import lombok.Builder;

@Builder
public record RegisteredUserDto(
        String name,
        String email) {
}
