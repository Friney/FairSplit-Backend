package com.friney.fairsplit.core.service.jwt.version;

public interface JwtVersionService {

    void createInitialVersion(Long userId);

    void incrementVersion(Long userId);

    Long getCurrentVersion(String email);

    boolean isValidVersion(String email, Long tokenVersion);

    boolean isExists(Long userId);
}