package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.summary.ReceiptSummaryDto;
import com.friney.fairsplit.api.dto.summary.SummaryDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.summary.Debt;
import com.friney.fairsplit.core.entity.summary.Summary;
import com.friney.fairsplit.core.entity.user.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.SummaryMapper;
import com.friney.fairsplit.core.mapper.UserMapper;
import com.friney.fairsplit.core.service.event.EventService;
import com.friney.fairsplit.core.service.summary.SummaryServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryServiceImplTest {

    @Mock
    private EventService eventService;

    @Mock
    private SummaryMapper summaryMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SummaryServiceImpl summaryService;

    @Test
    void testCalculateSummary() {
        User user1 = User.builder()
                .id(1L)
                .name("controller 1")
                .build();
        User user2 = User.builder()
                .id(2L)
                .name("controller 2")
                .build();
        User user3 = User.builder()
                .id(3L)
                .name("controller 3")
                .build();
        UserDto userDto1 = UserDto.builder().id(1L).name("controller 1").displayName("controller 1").build();
        UserDto userDto2 = UserDto.builder().id(2L).name("controller 2").displayName("controller 2").build();
        UserDto userDto3 = UserDto.builder().id(3L).name("controller 3").displayName("controller 3").build();
        when(userMapper.mapUser(user1)).thenReturn(userDto1);
        when(userMapper.mapUser(user2)).thenReturn(userDto2);
        when(userMapper.mapUser(user3)).thenReturn(userDto3);
        Expense expense1 = Expense.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .expenseMembers(Set.of(
                        ExpenseMember.builder().user(user1).build(),
                        ExpenseMember.builder().user(user2).build()
                ))
                .build();
        Expense expense2 = Expense.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(200))
                .expenseMembers(Set.of(
                        ExpenseMember.builder().user(user2).build(),
                        ExpenseMember.builder().user(user3).build()
                ))
                .build();
        Receipt receipt1 = Receipt.builder()
                .id(1L)
                .name("receipt 1")
                .paidByUser(user1)
                .expenses(Set.of(expense1, expense2))
                .build();
        Expense expense3 = Expense.builder()
                .id(3L)
                .amount(BigDecimal.valueOf(150))
                .expenseMembers(Set.of(
                        ExpenseMember.builder().user(user1).build(),
                        ExpenseMember.builder().user(user2).build(),
                        ExpenseMember.builder().user(user3).build()
                ))
                .build();
        Receipt receipt2 = Receipt.builder()
                .id(2L)
                .name("receipt 2")
                .paidByUser(user2)
                .expenses(Set.of(expense3))
                .build();
        Event event = Event.builder()
                .id(1L)
                .receipts(Set.of(receipt1, receipt2))
                .build();
        when(eventService.getById(1L)).thenReturn(event);
        Debt debt1 = Debt.builder().from(userDto2).to(userDto1).amount(BigDecimal.valueOf(150)).build();
        Debt debt2 = Debt.builder().from(userDto3).to(userDto1).amount(BigDecimal.valueOf(100)).build();
        Debt debt3 = Debt.builder().from(userDto1).to(userDto2).amount(BigDecimal.valueOf(50)).build();
        Debt debt4 = Debt.builder().from(userDto3).to(userDto2).amount(BigDecimal.valueOf(50)).build();
        ReceiptSummaryDto receiptSummaryDto1 = ReceiptSummaryDto.builder()
                .name("receipt 1")
                .total(BigDecimal.valueOf(300))
                .payerInfos(List.of())
                .expenses(List.of())
                .build();
        ReceiptSummaryDto receiptSummaryDto2 = ReceiptSummaryDto.builder()
                .name("receipt 2")
                .total(BigDecimal.valueOf(150))
                .payerInfos(List.of())
                .expenses(List.of())
                .build();
        SummaryDto summaryDto = SummaryDto.builder()
                .total(BigDecimal.valueOf(450))
                .debts(List.of(debt1, debt2, debt3, debt4))
                .receipts(List.of(receiptSummaryDto1, receiptSummaryDto2))
                .build();
        when(summaryMapper.map(any(Summary.class))).thenReturn(summaryDto);
        SummaryDto result = summaryService.calculateSummary(1L);
        assertEquals(BigDecimal.valueOf(450).stripTrailingZeros(), result.total().stripTrailingZeros());
        assertEquals(2, result.receipts().size());
        // Проверяем долги по событию (SummaryDto)
        assertEquals(4, result.debts().size());
        Debt d1 = result.debts().getFirst();
        assertEquals("controller 2", d1.getFrom().name());
        assertEquals("controller 1", d1.getTo().name());
        assertEquals(BigDecimal.valueOf(150).stripTrailingZeros(), d1.getAmount().stripTrailingZeros());
        Debt d2 = result.debts().get(1);
        assertEquals("controller 3", d2.getFrom().name());
        assertEquals("controller 1", d2.getTo().name());
        assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), d2.getAmount().stripTrailingZeros());
        Debt d3 = result.debts().get(2);
        assertEquals("controller 1", d3.getFrom().name());
        assertEquals("controller 2", d3.getTo().name());
        assertEquals(BigDecimal.valueOf(50).stripTrailingZeros(), d3.getAmount().stripTrailingZeros());
        Debt d4 = result.debts().get(3);
        assertEquals("controller 3", d4.getFrom().name());
        assertEquals("controller 2", d4.getTo().name());
        assertEquals(BigDecimal.valueOf(50).stripTrailingZeros(), d4.getAmount().stripTrailingZeros());
        // Проверяем receipts (name, total)
        ReceiptSummaryDto receiptSummary = result.receipts().getFirst();
        assertEquals("receipt 1", receiptSummary.name());
        assertEquals(BigDecimal.valueOf(300).stripTrailingZeros(), receiptSummary.total().stripTrailingZeros());
        receiptSummary = result.receipts().get(1);
        assertEquals("receipt 2", receiptSummary.name());
        assertEquals(BigDecimal.valueOf(150).stripTrailingZeros(), receiptSummary.total().stripTrailingZeros());
    }

    @Test
    void testCalculateSummaryNoReceipts() {
        Event event = Event.builder()
                .id(1L)
                .receipts(Set.of())
                .build();
        when(eventService.getById(1L)).thenReturn(event);
        SummaryDto summaryDto = SummaryDto.builder()
                .total(BigDecimal.ZERO)
                .receipts(List.of())
                .build();
        when(summaryMapper.map(any(Summary.class))).thenReturn(summaryDto);
        SummaryDto result = summaryService.calculateSummary(1L);
        assertEquals(BigDecimal.ZERO, result.total().stripTrailingZeros());
        assertEquals(0, result.receipts().size());
    }

    @Test
    void testCalculateSummaryEventNotFound() {
        when(eventService.getById(1L))
                .thenThrow(new ServiceException("event with id 1 not found", HttpStatus.NOT_FOUND));
        ServiceException exception = assertThrows(ServiceException.class, () -> summaryService.calculateSummary(1L));
        assertEquals("event with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }
}
