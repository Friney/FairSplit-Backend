package com.friney.fairsplit.core.mapper;


import com.friney.fairsplit.api.dto.summary.SummaryDto;
import com.friney.fairsplit.core.entity.summary.Summary;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ReceiptSummaryMapper.class)
public interface SummaryMapper {

    SummaryDto map(Summary summary);

    List<SummaryDto> map(List<Summary> summaries);
}
