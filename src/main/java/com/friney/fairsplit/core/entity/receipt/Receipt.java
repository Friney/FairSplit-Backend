package com.friney.fairsplit.core.entity.receipt;

import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.expense.Expense;
import com.friney.fairsplit.core.entity.user.User;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
@Entity
@Table(name = "receipts")
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(
        name = "Receipt.withExpenses",
        attributeNodes = {
                @NamedAttributeNode(value = "expenses", subgraph = "expensesSubgraph"),
                @NamedAttributeNode("paidByUser")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "expensesSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "expenseMembers", subgraph = "expenseMembersSubgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "expenseMembersSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode("user")
                        }
                )
        }
)
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @OneToMany(mappedBy = "receipt")
    Set<Expense> expenses;
    @ManyToOne
    @JoinColumn(name = "paid_by_user_id")
    User paidByUser;
}
