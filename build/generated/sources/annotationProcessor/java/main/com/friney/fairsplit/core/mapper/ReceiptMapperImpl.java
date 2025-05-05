package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.entity.receipt.Receipt;
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
public class ReceiptMapperImpl implements ReceiptMapper {

    @Autowired
    private ExpenseMapper expenseMapper;

    @Override
    public ReceiptDto map(Receipt receipt) {
        if ( receipt == null ) {
            return null;
        }

        ReceiptDto.ReceiptDtoBuilder receiptDto = ReceiptDto.builder();

        receiptDto.id( receipt.getId() );
        receiptDto.name( receipt.getName() );
        receiptDto.expenses( expenseSetToExpenseDtoList( receipt.getExpenses() ) );

        receiptDto.paidByUserName( receipt.getPaidByUser().getName() );

        return receiptDto.build();
    }

    @Override
    public List<ReceiptDto> map(List<Receipt> receipts) {
        if ( receipts == null ) {
            return null;
        }

        List<ReceiptDto> list = new ArrayList<ReceiptDto>( receipts.size() );
        for ( Receipt receipt : receipts ) {
            list.add( map( receipt ) );
        }

        return list;
    }

    protected List<ExpenseDto> expenseSetToExpenseDtoList(Set<Expense> set) {
        if ( set == null ) {
            return null;
        }

        List<ExpenseDto> list = new ArrayList<ExpenseDto>( set.size() );
        for ( Expense expense : set ) {
            list.add( expenseMapper.map( expense ) );
        }

        return list;
    }
}
