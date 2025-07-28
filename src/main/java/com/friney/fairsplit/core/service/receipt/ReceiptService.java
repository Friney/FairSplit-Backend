package com.friney.fairsplit.core.service.receipt;

import com.friney.fairsplit.api.dto.receipt.ReceiptCreateRequest;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptUpdateRequest;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface ReceiptService {

    List<ReceiptDto> getAllByEventId(Long eventId);

    ReceiptDto getDtoById(Long id);

    Receipt getById(Long id);

    ReceiptDto create(ReceiptCreateRequest receiptCreateRequest, Long eventId);

    ReceiptDto update(ReceiptUpdateRequest receiptUpdateRequest, Long id, Long eventId, UserDetails userDetails);

    void delete(Long id, Long eventId, UserDetails userDetails);

    boolean isExists(Long id);
}
