package com.friney.fairsplit.api.dto.User;

import lombok.Builder;

@Builder
public record NotRegisteredUserDto(
        String name) {
}
