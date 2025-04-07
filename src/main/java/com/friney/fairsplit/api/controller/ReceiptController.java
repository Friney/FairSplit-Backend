package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.Receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.Receipt.ReceiptDto;
import com.friney.fairsplit.core.service.receipt.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Paths.RECEIPTS)
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping
    public List<ReceiptDto> getAll(@PathVariable Long eventId) {
        return receiptService.getAllByEventId(eventId);
    }

    @PostMapping
    public ReceiptDto createReceipt(@RequestBody ReceiptCreateDto receiptCreateDto, @PathVariable Long eventId) {
        return receiptService.create(receiptCreateDto, eventId);
    }
}
