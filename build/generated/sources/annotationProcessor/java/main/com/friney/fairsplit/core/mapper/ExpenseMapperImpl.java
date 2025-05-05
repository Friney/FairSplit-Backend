package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberDto;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T18:27:50+0700",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 21.0.3 (Eclipse Adoptium)"
)
@Component
public class ExpenseMapperImpl implements ExpenseMapper {

    @Autowired
    private ExpenseMemberMapper expenseMemberMapper;

    @Override
    public ExpenseDto map(Expense expense) {
        if ( expense == null ) {
            return null;
        }

        ExpenseDto.ExpenseDtoBuilder expenseDto = ExpenseDto.builder();

        expenseDto.id( expense.getId() );
        expenseDto.name( expense.getName() );
        expenseDto.amount( expense.getAmount() );
        expenseDto.expenseMembers( expenseMemberSetToExpenseMemberDtoList( expense.getExpenseMembers() ) );

        return expenseDto.build();
    }

    @Override
    public List<ExpenseDto> map(List<Expense> expenses) {
        if ( expenses == null ) {
            return null;
        }

        List<ExpenseDto> list = new ArrayList<ExpenseDto>( expenses.size() );
        for ( Expense expense : expenses ) {
            list.add( map( expense ) );
        }

        return list;
    }

    protected List<ExpenseMemberDto> expenseMemberSetToExpenseMemberDtoList(Set<ExpenseMember> set) {
        if ( set == null ) {
            return null;
        }

        List<ExpenseMemberDto> list = new ArrayList<ExpenseMemberDto>( set.size() );
        for ( ExpenseMember expenseMember : set ) {
            list.add( expenseMemberMapper.map( expenseMember ) );
        }

        return list;
    }
}
