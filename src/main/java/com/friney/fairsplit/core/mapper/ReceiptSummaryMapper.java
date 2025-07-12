package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.summary.ReceiptSummaryDto;
import com.friney.fairsplit.core.entity.summary.ReceiptSummary;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ExpenseSummaryMapper.class)
public interface ReceiptSummaryMapper {
    ReceiptSummaryDto map(ReceiptSummary receiptSummary);

    List<ReceiptSummaryDto> map(List<ReceiptSummary> receiptSummaries);
}
