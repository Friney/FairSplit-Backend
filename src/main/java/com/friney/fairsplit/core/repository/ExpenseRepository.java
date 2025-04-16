package com.friney.fairsplit.core.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import com.friney.fairsplit.core.entity.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Override
    @EntityGraph(value = "Expense.withExpenseMembers", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Expense> findById(Long id);
}
