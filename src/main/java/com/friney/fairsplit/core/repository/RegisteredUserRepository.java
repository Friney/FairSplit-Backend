package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.User.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {
}
