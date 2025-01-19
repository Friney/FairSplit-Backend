package com.friney.fairsplit.api.dto.Event;

import com.friney.fairsplit.api.dto.Receipt.ReceiptDto;
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
