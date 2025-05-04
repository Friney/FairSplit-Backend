package com.friney.fairsplit.core.entity.expense;

import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@Getter
@Setter
@Entity
@Table(name = "expenses")
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(
        name = "Expense.withExpenseMembers",
        attributeNodes = {
                @NamedAttributeNode(value = "expenseMembers", subgraph = "expenseMembersSubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "expenseMembersSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode("user")
                        }
                )
        }
)
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "receipt_id")
    Receipt receipt;
    @OneToMany(mappedBy = "expense",
            cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    Set<ExpenseMember> expenseMembers;
}

