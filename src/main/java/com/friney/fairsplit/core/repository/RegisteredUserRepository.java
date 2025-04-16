package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.user.RegisteredUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {

    Optional<RegisteredUser> findByEmail(String email);
}
