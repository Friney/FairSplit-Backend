package com.friney.fairsplit.api.dto.user;

import lombok.Builder;

@Builder
public record NotRegisteredUserDto(
        String name) {
}
