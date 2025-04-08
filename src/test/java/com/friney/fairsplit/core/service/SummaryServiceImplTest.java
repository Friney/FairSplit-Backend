package com.friney.fairsplit.core.service;

import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.entity.expense_member.ExpenseMember;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.summary.Debt;
import com.friney.fairsplit.core.entity.summary.ReceiptSummary;
import com.friney.fairsplit.core.entity.summary.Summary;
import com.friney.fairsplit.core.entity.user.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.service.event.EventService;
import com.friney.fairsplit.core.service.summary.SummaryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryServiceImplTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private SummaryServiceImpl summaryService;

    @Test
    void testCalculateSummary() {
        User user1 = User.builder()
                .id(1L)
                .name("user 1")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("user 2")
                .build();

        User user3 = User.builder()
                .id(3L)
                .name("user 3")
                .build();

        Expense expense1 = Expense.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .expenseMembers(Arrays.asList(
                        ExpenseMember.builder()
                                .user(user1)
                                .build(),
                        ExpenseMember.builder()
                                .user(user2)
                                .build()
                ))
                .build();

        Expense expense2 = Expense.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(200))
                .expenseMembers(Arrays.asList(
                        ExpenseMember.builder()
                                .user(user2)
                                .build(),
                        ExpenseMember.builder()
                                .user(user3)
                                .build()
                ))
                .build();

        Receipt receipt1 = Receipt.builder()
                .id(1L)
                .paidByUser(user1)
                .expenses(Arrays.asList(expense1, expense2))
                .build();

        Expense expense3 = Expense.builder()
                .id(3L)
                .amount(BigDecimal.valueOf(150))
                .expenseMembers(Arrays.asList(
                        ExpenseMember.builder()
                                .user(user1)
                                .build(),
                        ExpenseMember.builder()
                                .user(user2)
                                .build(),
                        ExpenseMember.builder()
                                .user(user3)
                                .build()
                ))
                .build();

        Receipt receipt2 = Receipt.builder()
                .id(2L)
                .paidByUser(user2)
                .expenses(List.of(expense3))
                .build();

        Event event = Event.builder()
                .id(1L)
                .receipts(Arrays.asList(receipt1, receipt2))
                .build();

        when(eventService.getById(1L)).thenReturn(event);

        Summary result = summaryService.calculateSummary(1L);

        assertEquals(BigDecimal.valueOf(450).stripTrailingZeros(), result.getTotal().stripTrailingZeros());
        assertEquals(2, result.getReceipts().size());

        ReceiptSummary receipt1Summary = result.getReceipts().getFirst();
        assertEquals(BigDecimal.valueOf(300).stripTrailingZeros(), receipt1Summary.getTotal().stripTrailingZeros());
        assertEquals(3, receipt1Summary.getDebts().size());

        Debt debt1 = receipt1Summary.getDebts().getFirst();
        assertEquals("user 1", debt1.getFrom());
        assertEquals("user 1", debt1.getTo());
        assertEquals(BigDecimal.valueOf(50).stripTrailingZeros(), debt1.getAmount().stripTrailingZeros());

        Debt debt2 = receipt1Summary.getDebts().get(1);
        assertEquals("user 2", debt2.getFrom());
        assertEquals("user 1", debt2.getTo());
        assertEquals(BigDecimal.valueOf(150).stripTrailingZeros(), debt2.getAmount().stripTrailingZeros());

        Debt debt3 = receipt1Summary.getDebts().get(2);
        assertEquals("user 3", debt3.getFrom());
        assertEquals("user 1", debt3.getTo());
        assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), debt3.getAmount().stripTrailingZeros());

        ReceiptSummary receipt2Summary = result.getReceipts().get(1);
        assertEquals(BigDecimal.valueOf(150).stripTrailingZeros(), receipt2Summary.getTotal().stripTrailingZeros());
        assertEquals(3, receipt2Summary.getDebts().size());

        Debt debt4 = receipt2Summary.getDebts().getFirst();
        assertEquals("user 1", debt4.getFrom());
        assertEquals("user 2", debt4.getTo());
        assertEquals(BigDecimal.valueOf(50).stripTrailingZeros(), debt4.getAmount().stripTrailingZeros());

        Debt debt5 = receipt2Summary.getDebts().get(1);
        assertEquals("user 2", debt5.getFrom());
        assertEquals("user 2", debt5.getTo());
        assertEquals(BigDecimal.valueOf(50).stripTrailingZeros(), debt5.getAmount().stripTrailingZeros());

        Debt debt6 = receipt2Summary.getDebts().get(2);
        assertEquals("user 3", debt6.getFrom());
        assertEquals("user 2", debt6.getTo());
        assertEquals(BigDecimal.valueOf(50).stripTrailingZeros(), debt6.getAmount().stripTrailingZeros());
    }

    @Test
    void testCalculateSummaryNoReceipts() {
        Event event = Event.builder()
                .id(1L)
                .receipts(List.of())
                .build();

        when(eventService.getById(1L)).thenReturn(event);

        Summary result = summaryService.calculateSummary(1L);

        assertEquals(BigDecimal.ZERO, result.getTotal().stripTrailingZeros());
        assertEquals(0, result.getReceipts().size());
    }

    @Test
    void testCalculateSummaryEventNotFound() {
        when(eventService.getById(1L))
                .thenThrow(new ServiceException("event with id 1 not found", HttpStatus.NOT_FOUND));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            summaryService.calculateSummary(1L);
        });

        assertEquals("event with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }
}
