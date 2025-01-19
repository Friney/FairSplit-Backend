package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.Expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
