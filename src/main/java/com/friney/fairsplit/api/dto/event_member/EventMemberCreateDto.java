package com.friney.fairsplit.api.dto.event_member;

import lombok.Builder;

@Builder
public record EventMemberCreateDto(
        Long userid) {
}
