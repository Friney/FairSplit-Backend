package com.friney.fairsplit.core.repository;

import com.friney.fairsplit.core.entity.jwtversion.JwtVersion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtVersionRepository extends JpaRepository<JwtVersion, Long> {

    Optional<JwtVersion> findByUserId(Long userId);

    boolean existsByUserId(Long id);

    void deleteByUserId(Long userId);
}