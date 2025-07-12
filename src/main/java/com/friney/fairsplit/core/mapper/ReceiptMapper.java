package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ExpenseMapper.class, UserMapper.class})
public interface ReceiptMapper {
    ReceiptDto map(Receipt receipt);

    List<ReceiptDto> map(List<Receipt> receipts);
}
