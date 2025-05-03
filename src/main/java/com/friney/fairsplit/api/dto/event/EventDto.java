package com.friney.fairsplit.api.dto.event;

import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import lombok.Builder;

import java.util.List;

@Builder
public record EventDto(
        Long id,
        String name,
        String description,
        List<ReceiptDto> receipts
) {
}
