package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Override
    @EntityGraph(value = "Event.withReceiptsAndExpenses", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Event> findById(Long id);

    @EntityGraph(value = "Event.withReceiptsAndExpenses", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findAllByOwner(RegisteredUser owner, Sort sort);
}
