package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpenseMemberMapper {
    @Mapping(target = "name", expression = "java(expenseMember.getUser().getName())")
    ExpenseMemberDto map(ExpenseMember expenseMember);

    List<ExpenseMemberDto> map(List<ExpenseMember> expenseMembers);
}
