package com.friney.fairsplit.core.service.receipt;

import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ReceiptMapper;
import com.friney.fairsplit.core.repository.ReceiptRepository;
import com.friney.fairsplit.core.service.event.EventService;
import com.friney.fairsplit.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final EventService eventService;
    private final UserService userService;
    private final ReceiptMapper receiptMapper;

    @Override
    public List<ReceiptDto> getAllByEventId(Long eventId) {
        EventDto event = eventService.getDtoById(eventId);
        return event.receipts();
    }

    @Override
    public ReceiptDto getDtoById(Long id) {
        return receiptMapper.map(getById(id));
    }

    @Override
    public Receipt getById(Long id) {
        return receiptRepository.findById(id)
                .orElseThrow(() -> new ServiceException("receipt with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public ReceiptDto create(ReceiptCreateDto receiptCreateDto, Long eventId) {
        Receipt receipt = Receipt.builder()
                .name(receiptCreateDto.name())
                .event(eventService.getById(eventId))
                .paidByUser(userService.getById(receiptCreateDto.userId()))
                .build();
        return receiptMapper.map(receiptRepository.save(receipt));
    }
}
