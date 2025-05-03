package com.friney.fairsplit.core.entity.event;

import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
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
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(
        name = "Event.withReceiptsAndExpenses",
        attributeNodes = {
                @NamedAttributeNode(value = "receipts", subgraph = "receiptsSubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "receiptsSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode("paidByUser"),
                                @NamedAttributeNode(value = "expenses", subgraph = "expensesSubgraph")
                        }
                ),
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
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    @OneToMany(mappedBy = "event")
    Set<Receipt> receipts;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    RegisteredUser owner;
}
