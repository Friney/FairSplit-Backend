package com.friney.fairsplit.core.service;

import com.friney.fairsplit.core.entity.Summary.Debt;
import com.friney.fairsplit.core.entity.ExpenseMember.ExpenseMember;
import com.friney.fairsplit.core.entity.Summary.ReceiptSummary;
import com.friney.fairsplit.core.entity.Event.Event;
import com.friney.fairsplit.core.entity.Expense.Expense;
import com.friney.fairsplit.core.entity.Receipt.Receipt;
import com.friney.fairsplit.core.entity.Summary.Summary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SummaryService {
    private final EventService eventService;

    public Summary calculateSummary(Long eventId) {
        Summary summary = new Summary();
        Event event = eventService.getById(eventId);
        Set<Receipt> receipts = event.getReceipts();
        List<ReceiptSummary> receiptSummaries = new ArrayList<>();
        for (Receipt receipt : receipts) {
            String recipient = receipt.getPaidByUser().getName();
            Set<Expense> expenses = receipt.getExpenses();

            BigDecimal total = expenses
                    .stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, BigDecimal> debtsMap = new HashMap<>();
            for (Expense expense : expenses) {
                BigDecimal amount = expense.getAmount();
                int countMembers = expense.getExpenseMembers().size();
                BigDecimal amountPerMember = amount.divide(BigDecimal.valueOf(countMembers), 2, RoundingMode.HALF_UP);
                for (ExpenseMember expenseMember : expense.getExpenseMembers()) {
                    String name = expenseMember.getUser().getName();
                    debtsMap.merge(name, amountPerMember, BigDecimal::add);
                }
            }

            List<Debt> debts = new ArrayList<>();
            for (Map.Entry<String, BigDecimal> entry : debtsMap.entrySet()) {
                debts.add(new Debt(entry.getKey(), recipient, entry.getValue()));
            }
            receiptSummaries.add(new ReceiptSummary(total, debts));
        }
        summary.setReceipts(receiptSummaries);
        BigDecimal total = receiptSummaries
                .stream()
                .map(ReceiptSummary::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotal(total);
        return summary;
    }
}
