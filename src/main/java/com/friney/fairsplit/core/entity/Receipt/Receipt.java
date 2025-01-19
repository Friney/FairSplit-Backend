package com.friney.fairsplit.core.entity.Receipt;

import com.friney.fairsplit.core.entity.Event.Event;
import com.friney.fairsplit.core.entity.Expense.Expense;
import com.friney.fairsplit.core.entity.User.User;
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

import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "receipts")
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @OneToMany(mappedBy = "receipt")
    List<Expense> expenses;
    @ManyToOne
    @JoinColumn(name = "paid_by_user_id")
    User paidByUser;
}