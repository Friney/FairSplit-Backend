package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptUpdateDto;
import com.friney.fairsplit.core.service.receipt.ReceiptService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Paths.RECEIPTS)
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping
    public List<ReceiptDto> getAllByEventId(@PathVariable Long eventId) {
        return receiptService.getAllByEventId(eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReceiptDto createReceipt(@RequestBody ReceiptCreateDto receiptCreateDto, @PathVariable Long eventId) {
        return receiptService.create(receiptCreateDto, eventId);
    }

    @PatchMapping("{id}")
    public ReceiptDto update(@RequestBody ReceiptUpdateDto receiptUpdateDto, @PathVariable Long id,
                             @PathVariable Long eventId, @AuthenticationPrincipal UserDetails userDetails) {
        return receiptService.update(receiptUpdateDto, id, eventId, userDetails);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @PathVariable Long eventId, @AuthenticationPrincipal UserDetails userDetails) {
        receiptService.delete(id, eventId, userDetails);
    }
}
