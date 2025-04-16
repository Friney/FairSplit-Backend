package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.user.NotRegisteredUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotRegisteredUserRepository extends JpaRepository<NotRegisteredUser, Long> {
    Optional<Object> findByName(String name);
}
