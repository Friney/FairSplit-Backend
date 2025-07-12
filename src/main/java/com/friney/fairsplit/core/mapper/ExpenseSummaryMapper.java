package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.summary.ExpenseSummaryDto;
import com.friney.fairsplit.core.entity.summary.ExpenseSummary;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpenseSummaryMapper {
    ExpenseSummaryDto map(ExpenseSummary expenseSummary);

    List<ExpenseSummaryDto> map(List<ExpenseSummary> expenseSummaries);
}
