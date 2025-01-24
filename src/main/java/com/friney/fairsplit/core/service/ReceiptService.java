package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.Event.EventDto;
import com.friney.fairsplit.api.dto.Receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.Receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.Receipt.Receipt;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ReceiptMapper;
import com.friney.fairsplit.core.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final EventService eventService;
    private final UserService userService;
    private final ReceiptMapper receiptMapper;

    public List<ReceiptDto> getAll(Long eventId) {
        EventDto event = eventService.getDtoById(eventId);
        return event.receipts();
    }

    public ReceiptDto getDtoById(Long id) {
        return receiptMapper.map(getById(id));
    }

    public Receipt getById(Long id) {
        return receiptRepository.findById(id).orElseThrow(() -> new ServiceException("Receipt with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    public ReceiptDto create(ReceiptCreateDto receiptCreateDto, Long eventId) {
        Receipt receipt = new Receipt();
        receipt.setName(receiptCreateDto.name());
        receipt.setEvent(eventService.getById(eventId));
        receipt.setPaidByUser(userService.getById(receiptCreateDto.userId()));
        receipt = receiptRepository.save(receipt);
        return receiptMapper.map(receipt);
    }
}
