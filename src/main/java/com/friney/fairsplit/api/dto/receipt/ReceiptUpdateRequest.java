package com.friney.fairsplit.api.dto.receipt;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ReceiptUpdateRequest(
        @Nullable String name,
        @Nullable Long userId
) {
}
