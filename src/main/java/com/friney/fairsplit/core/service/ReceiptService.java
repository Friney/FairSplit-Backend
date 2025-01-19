package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.Receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.Receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.Receipt.Receipt;
import com.friney.fairsplit.core.mapper.ReceiptMapper;
import com.friney.fairsplit.core.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
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
        return receiptRepository
                .findAll()
                .stream()
                .filter(
                        receipt -> receipt
                                .getEvent()
                                .getId()
                                .equals(eventId)
                )
                .map(receiptMapper::map)
                .toList();
    }

    public Receipt getById(Long id) {
        return receiptRepository.findById(id).orElse(null);
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
