package com.friney.fairsplit.core.service.summary;

import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.entity.expense_member.ExpenseMember;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.summary.Debt;
import com.friney.fairsplit.core.entity.summary.ReceiptSummary;
import com.friney.fairsplit.core.entity.summary.Summary;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.service.event.EventService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final EventService eventService;

    @Override
    public Summary calculateSummary(Long eventId) {
        Summary summary = new Summary();
        Event event = eventService.getById(eventId);
        List<Receipt> receipts = event.getReceipts();
        if (receipts == null || receipts.isEmpty()) {
            summary.setTotal(BigDecimal.ZERO);
            summary.setReceipts(List.of());
            return summary;
        }

        summary.setReceipts(getReceiptSummaries(receipts));
        BigDecimal total = summary.getReceipts()
                .stream()
                .map(ReceiptSummary::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotal(total);
        return summary;
    }

    private List<ReceiptSummary> getReceiptSummaries(List<Receipt> receipts) {
        List<ReceiptSummary> receiptSummaries = new ArrayList<>();
        for (Receipt receipt : receipts) {
            String recipient = receipt.getPaidByUser().getName();
            List<Expense> expenses = receipt.getExpenses();
            if (expenses == null || expenses.isEmpty()) {
                continue;
            }

            BigDecimal receiptTotal = expenses
                    .stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, BigDecimal> debtsMap = new HashMap<>();
            for (Expense expense : expenses) {
                BigDecimal amountPerMember = getBigDecimalPerMember(expense);
                for (ExpenseMember expenseMember : expense.getExpenseMembers()) {
                    String name = expenseMember.getUser().getName();
                    if (name.equals(recipient)) {
                        continue;
                    }
                    debtsMap.merge(name, amountPerMember, BigDecimal::add);
                }
            }

            List<Debt> debts = new ArrayList<>();
            for (Map.Entry<String, BigDecimal> entry : debtsMap.entrySet()) {
                debts.add(new Debt(entry.getKey(), recipient, entry.getValue()));
            }
            debts.sort(Comparator.comparing(debt -> debt.getFrom().toLowerCase()));
            receiptSummaries.add(new ReceiptSummary(receiptTotal, debts));
        }
        return receiptSummaries;
    }

    private BigDecimal getBigDecimalPerMember(Expense expense) {
        BigDecimal amount = expense.getAmount();
        int countMembers = expense.getExpenseMembers().size();
        if (countMembers == 0) {
            return amount;
        }
        return amount.divide(BigDecimal.valueOf(countMembers), 2, RoundingMode.HALF_UP);
    }
}

