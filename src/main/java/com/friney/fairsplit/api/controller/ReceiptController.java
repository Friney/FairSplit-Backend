package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.Receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.Receipt.ReceiptDto;
import com.friney.fairsplit.core.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Paths.RECEIPTS)
@RequiredArgsConstructor
public class ReceiptController {
    private final ReceiptService receiptService;

    @GetMapping
    public List<ReceiptDto> getAll(@PathVariable Long eventId) {
        return receiptService.getAll(eventId);
    }

    @PostMapping
    public ReceiptDto createReceipt(@RequestBody ReceiptCreateDto receiptCreateDto, @PathVariable Long eventId) {
        return receiptService.create(receiptCreateDto, eventId);
    }
}
