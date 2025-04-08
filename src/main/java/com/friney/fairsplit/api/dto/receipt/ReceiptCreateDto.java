package com.friney.fairsplit.api.dto.receipt;

import lombok.Builder;

@Builder
public record ReceiptCreateDto(
        String name,
        Long userId
) {
}
