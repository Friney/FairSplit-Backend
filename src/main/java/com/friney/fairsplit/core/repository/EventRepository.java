package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.Event.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Override
    @EntityGraph(value = "Event.withReceiptsAndExpenses", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findAll();

    @Override
    @EntityGraph(value = "Event.withReceiptsAndExpenses", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Event> findById(Long id);
}
