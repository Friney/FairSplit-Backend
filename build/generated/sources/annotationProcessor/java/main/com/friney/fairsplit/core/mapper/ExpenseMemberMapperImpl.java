package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T18:27:50+0700",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 21.0.3 (Eclipse Adoptium)"
)
@Component
public class ExpenseMemberMapperImpl implements ExpenseMemberMapper {

    @Override
    public ExpenseMemberDto map(ExpenseMember expenseMember) {
        if ( expenseMember == null ) {
            return null;
        }

        ExpenseMemberDto.ExpenseMemberDtoBuilder expenseMemberDto = ExpenseMemberDto.builder();

        expenseMemberDto.name( expenseMember.getUser().getName() );

        return expenseMemberDto.build();
    }

    @Override
    public List<ExpenseMemberDto> map(List<ExpenseMember> expenseMembers) {
        if ( expenseMembers == null ) {
            return null;
        }

        List<ExpenseMemberDto> list = new ArrayList<ExpenseMemberDto>( expenseMembers.size() );
        for ( ExpenseMember expenseMember : expenseMembers ) {
            list.add( map( expenseMember ) );
        }

        return list;
    }
}
