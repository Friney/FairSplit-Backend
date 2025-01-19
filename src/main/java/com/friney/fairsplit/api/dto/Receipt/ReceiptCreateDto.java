package com.friney.fairsplit.api.dto.Receipt;

import lombok.Builder;

@Builder
public record ReceiptCreateDto(
        String name,
        Long userId
) {
}
