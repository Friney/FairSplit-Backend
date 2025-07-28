package com.friney.fairsplit.core.service.summary;

import com.friney.fairsplit.api.dto.summary.SummaryDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.summary.Debt;
import com.friney.fairsplit.core.entity.summary.ExpenseSummary;
import com.friney.fairsplit.core.entity.summary.PayerInfo;
import com.friney.fairsplit.core.entity.summary.ReceiptSummary;
import com.friney.fairsplit.core.entity.summary.Summary;
import com.friney.fairsplit.core.entity.user.User;
import com.friney.fairsplit.core.mapper.SummaryMapper;
import com.friney.fairsplit.core.mapper.UserMapper;
import com.friney.fairsplit.core.service.event.EventService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final EventService eventService;
    private final SummaryMapper summaryMapper;
    private final UserMapper userMapper;

    @Override
    public SummaryDto calculateSummary(Long eventId) {
        Summary summary = new Summary();
        Event event = eventService.getById(eventId);
        Set<Receipt> receipts = event.getReceipts();
        if (receipts == null || receipts.isEmpty()) {
            summary.setTotal(BigDecimal.ZERO);
            summary.setReceipts(List.of());
            summary.setDebts(List.of());
            summary.setPayerInfos(List.of());
            return summaryMapper.map(summary);
        }

        summary.setReceipts(getReceiptSummaries(receipts));
        BigDecimal total = summary.getReceipts()
                .stream()
                .map(ReceiptSummary::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotal(total);
        DebtAndPayerInfo debtAndPayerInfo = getDebtAndPayerInfo(summary.getReceipts());
        summary.setDebts(debtAndPayerInfo.debts);
        summary.setPayerInfos(debtAndPayerInfo.payerInfos);
        return summaryMapper.map(summary);
    }

    private DebtAndPayerInfo getDebtAndPayerInfo(List<ReceiptSummary> receipts) {
        Map<PersonPair, BigDecimal> debtsMap = new HashMap<>();
        Map<UserDto, BigDecimal> payerInfoMap = new HashMap<>();
        for (ReceiptSummary receipt : receipts) {
            for (Debt debt : receipt.getDebts()) {
                debtsMap.merge(new PersonPair(debt.getFrom(), debt.getTo()), debt.getAmount(), BigDecimal::add);
                payerInfoMap.merge(debt.getFrom(), debt.getAmount(), BigDecimal::add);
            }
        }
        List<Debt> debts = new ArrayList<>();
        List<PayerInfo> payerInfos = new ArrayList<>();
        for (Map.Entry<PersonPair, BigDecimal> entry : debtsMap.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) == 0 || entry.getKey().from.equals(entry.getKey().to)) {
                continue;
            }
            debts.add(new Debt(entry.getKey().from, entry.getKey().to, entry.getValue()));
        }

        for (Map.Entry<UserDto, BigDecimal> entry : payerInfoMap.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            payerInfos.add(new PayerInfo(entry.getKey(), entry.getValue()));
        }
        debts.sort(Comparator.comparing(debt -> debt.getTo().displayName().toLowerCase()));
        payerInfos.sort(Comparator.comparing(payerInfo -> payerInfo.getUser().displayName().toLowerCase()));

        return new DebtAndPayerInfo(debts, payerInfos);
    }

    private List<ReceiptSummary> getReceiptSummaries(Set<Receipt> receipts) {
        List<ReceiptSummary> receiptSummaries = new ArrayList<>();
        for (Receipt receipt : receipts) {
            User recipient = receipt.getPaidByUser();
            UserDto recipientDto = userMapper.mapUser(recipient);
            Set<Expense> expenses = receipt.getExpenses();
            if (expenses == null || expenses.isEmpty()) {
                receiptSummaries.add(new ReceiptSummary(receipt.getName(), BigDecimal.ZERO, List.of(), List.of(), List.of()));
                continue;
            }
            List<ExpenseSummary> expenseSummary = getExpenseSummaries(expenses, recipient);
            BigDecimal receiptTotal = expenses
                    .stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<UserDto, BigDecimal> payerInfoMap = new HashMap<>();
            for (ExpenseSummary expense : expenseSummary) {
                for (Debt debt : expense.getDebts()) {
                    payerInfoMap.merge(debt.getFrom(), debt.getAmount(), BigDecimal::add);
                }
            }
            List<Debt> debts = new ArrayList<>();
            List<PayerInfo> payerInfos = new ArrayList<>();
            for (Map.Entry<UserDto, BigDecimal> entry : payerInfoMap.entrySet()) {
                debts.add(new Debt(entry.getKey(), recipientDto, entry.getValue()));
                payerInfos.add(new PayerInfo(entry.getKey(), entry.getValue()));
            }
            debts.sort(Comparator.comparing(debt -> debt.getTo().displayName().toLowerCase()));
            payerInfos.sort(Comparator.comparing(payerInfo -> payerInfo.getUser().displayName().toLowerCase()));

            receiptSummaries.add(new ReceiptSummary(receipt.getName(), receiptTotal, payerInfos, debts, expenseSummary));
        }
        receiptSummaries.sort(Comparator.comparing(receiptSummary -> receiptSummary.getName().toLowerCase()));
        return receiptSummaries;
    }

    private List<ExpenseSummary> getExpenseSummaries(Set<Expense> expenses, User recipient) {
        List<ExpenseSummary> expenseSummaries = new ArrayList<>();
        for (Expense expense : expenses) {
            Map<User, BigDecimal> payerInfoMap = new HashMap<>();
            BigDecimal amountPerMember = getBigDecimalPerMember(expense);
            BigDecimal total = expense.getAmount();
            String name = expense.getName();
            for (ExpenseMember expenseMember : expense.getExpenseMembers()) {
                User user = expenseMember.getUser();
                payerInfoMap.merge(user, amountPerMember, BigDecimal::add);
            }

            List<Debt> debts = new ArrayList<>();
            List<PayerInfo> payerInfos = new ArrayList<>();
            for (Map.Entry<User, BigDecimal> entry : payerInfoMap.entrySet()) {
                UserDto fromDto = userMapper.mapUser(entry.getKey());
                UserDto toDto = userMapper.mapUser(recipient);
                debts.add(new Debt(fromDto, toDto, entry.getValue()));
                payerInfos.add(new PayerInfo(fromDto, entry.getValue()));
            }
            debts.sort(Comparator.comparing(debt -> debt.getTo().displayName().toLowerCase()));
            payerInfos.sort(Comparator.comparing(payerInfo -> payerInfo.getUser().displayName().toLowerCase()));
            expenseSummaries.add(new ExpenseSummary(name, total, payerInfos, debts));
        }
        return expenseSummaries;
    }

    private BigDecimal getBigDecimalPerMember(Expense expense) {
        BigDecimal amount = expense.getAmount();
        int countMembers = expense.getExpenseMembers().size();
        if (countMembers == 0) {
            return BigDecimal.ZERO;
        }
        return amount.divide(BigDecimal.valueOf(countMembers), 2, RoundingMode.HALF_UP);
    }


    private record PersonPair(
            UserDto from,
            UserDto to
    ) {
    }

    private record DebtAndPayerInfo(
            List<Debt> debts,
            List<PayerInfo> payerInfos
    ) {
    }
}
