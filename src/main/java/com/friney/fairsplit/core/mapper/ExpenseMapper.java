package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.core.entity.expense.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ExpenseMemberMapper.class)
public interface ExpenseMapper {
    ExpenseDto map(Expense expense);

    List<ExpenseDto> map(List<Expense> expenses);
}
