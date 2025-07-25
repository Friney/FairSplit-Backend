package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.expense.Expense;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Override
    @EntityGraph(value = "Expense.withExpenseMembers", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Expense> findById(Long id);

    @EntityGraph(value = "Expense.withExpenseMembers", type = EntityGraph.EntityGraphType.LOAD)
    List<Expense> findAllByReceiptId(Long receiptId, Sort sort);
}
