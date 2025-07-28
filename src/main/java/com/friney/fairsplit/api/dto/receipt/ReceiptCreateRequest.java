package com.friney.fairsplit.api.dto.receipt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReceiptCreateRequest(
        @NotBlank String name,
        @NotNull Long userId
) {
}
