package com.friney.fairsplit.core.entity.expense;

import com.friney.fairsplit.core.entity.expense_member.ExpenseMember;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "expenses")
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "receipt_id")
    Receipt receipt;
    @OneToMany(mappedBy = "expense")
    List<ExpenseMember> expenseMembers;
}
