package com.friney.fairsplit.core.service.receipt;

import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptUpdateDto;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.ReceiptMapper;
import com.friney.fairsplit.core.repository.ReceiptRepository;
import com.friney.fairsplit.core.service.event.EventService;
import com.friney.fairsplit.core.service.user.UserService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
        List<ReceiptDto> receipts = event.receipts();
        receipts.sort(Comparator.comparing(ReceiptDto::id));
        return receipts;
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

    @Override
    public ReceiptDto update(ReceiptUpdateDto receiptUpdateDto, Long id, Long eventId, UserDetails userDetails) {
        Receipt receipt = getById(id);
        validateChangeRequest(receipt, eventId, userDetails);

        if (receiptUpdateDto.name() != null) {
            receipt.setName(receiptUpdateDto.name());
        }
        if (receiptUpdateDto.userId() != null) {
            receipt.setPaidByUser(userService.getById(receiptUpdateDto.userId()));
        }
        return receiptMapper.map(receiptRepository.save(receipt));
    }

    @Override
    public void delete(Long id, Long eventId, UserDetails userDetails) {
        Receipt receipt = getById(id);
        validateChangeRequest(receipt, eventId, userDetails);

        receiptRepository.delete(receipt);
    }

    boolean hasPermissionOnChange(Receipt receipt, UserDetails userDetails) {
        return receipt.getEvent().getOwner().getEmail().equals(userDetails.getUsername());
    }

    void validateChangeRequest(Receipt receipt, Long eventId, UserDetails userDetails) {
        if (!receipt.getEvent().getId().equals(eventId)) {
            throw new ServiceException("receipt with id " + receipt.getId() + " in event with id " + eventId + " not found", HttpStatus.NOT_FOUND);
        }
        if (!hasPermissionOnChange(receipt, userDetails)) {
            throw new ServiceException("you are not the owner of this receipt", HttpStatus.FORBIDDEN);
        }
    }
}
