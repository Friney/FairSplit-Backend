package com.friney.fairsplit.core.service.receipt;

import com.friney.fairsplit.api.dto.receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptUpdateDto;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface ReceiptService {

    List<ReceiptDto> getAllByEventId(Long eventId);

    ReceiptDto getDtoById(Long id);

    Receipt getById(Long id);

    ReceiptDto create(ReceiptCreateDto receiptCreateDto, Long eventId);

    ReceiptDto update(ReceiptUpdateDto receiptUpdateDto, Long id, Long eventId, UserDetails userDetails);

    void delete(Long id, Long eventId, UserDetails userDetails);

    boolean isExists(Long id);
}
