package com.friney.fairsplit.core.service.receipt;

import com.friney.fairsplit.api.dto.receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.receipt.Receipt;

import java.util.List;

public interface ReceiptService {

    List<ReceiptDto> getAllByEventId(Long eventId);

    ReceiptDto getDtoById(Long id);

    Receipt getById(Long id);

    ReceiptDto create(ReceiptCreateDto receiptCreateDto, Long eventId);
}
