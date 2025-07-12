package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserMapper.class)
public interface ExpenseMemberMapper {
    ExpenseMemberDto map(ExpenseMember expenseMember);

    List<ExpenseMemberDto> map(List<ExpenseMember> expenseMembers);
}
