package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.expense.member.ExpenseMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseMemberRepository extends JpaRepository<ExpenseMember, Long> {
}
