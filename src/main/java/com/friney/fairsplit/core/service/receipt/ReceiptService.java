package com.friney.fairsplit.core.service.receipt;

import com.friney.fairsplit.api.dto.Receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.Receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.Receipt.Receipt;

import java.util.List;

public interface ReceiptService {

    List<ReceiptDto> getAllByEventId(Long eventId);

    ReceiptDto getDtoById(Long id);

    Receipt getById(Long id);

    ReceiptDto create(ReceiptCreateDto receiptCreateDto, Long eventId);
}
