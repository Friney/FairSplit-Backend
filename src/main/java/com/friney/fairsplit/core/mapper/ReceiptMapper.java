package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ExpenseMapper.class)
public interface ReceiptMapper {
    @Mapping(target = "paidByUserName", expression = "java(receipt.getPaidByUser().getName())")
    ReceiptDto map(Receipt receipt);

    List<ReceiptDto> map(List<Receipt> receipts);
}
