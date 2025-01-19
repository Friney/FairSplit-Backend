package com.friney.fairsplit.api.dto.EventMember;

import lombok.Builder;

@Builder
public record EventMemberCreateDto(
        Long userid) {
}
