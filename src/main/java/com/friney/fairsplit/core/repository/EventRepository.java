package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.Event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
