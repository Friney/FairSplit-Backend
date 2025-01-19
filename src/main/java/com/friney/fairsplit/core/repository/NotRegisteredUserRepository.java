package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.User.NotRegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotRegisteredUserRepository extends JpaRepository<NotRegisteredUser, Long> {
}
