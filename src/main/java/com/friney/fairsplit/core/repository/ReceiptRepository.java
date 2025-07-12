package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.receipt.Receipt;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    @Override
    @EntityGraph(value = "Receipt.withExpenses", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Receipt> findById(Long id);
}
