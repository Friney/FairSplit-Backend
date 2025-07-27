package com.friney.fairsplit.core.service.jwt.version;

import com.friney.fairsplit.core.entity.jwtversion.JwtVersion;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.entity.user.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.repository.JwtVersionRepository;
import com.friney.fairsplit.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtVersionServiceImpl implements JwtVersionService {

    private final JwtVersionRepository jwtVersionRepository;
    private final UserService userService;

    @Override
    public void createInitialVersion(Long userId) {
        if (jwtVersionRepository.existsByUserId(userId)) {
            throw new ServiceException(
                    "Token version already exists for user: " + userId,
                    HttpStatus.BAD_REQUEST
            );
        }

        User user = User.builder()
                .id(userId)
                .build();
        JwtVersion version = JwtVersion.builder()
                .version(1L)
                .user(user)
                .build();

        jwtVersionRepository.save(version);
    }

    @Override
    public void incrementVersion(Long userId) {
        JwtVersion version = getVersionByUserId(userId);

        version.setVersion(version.getVersion() + 1);

        jwtVersionRepository.save(version);
    }

    @Override
    public Long getCurrentVersion(String email) {
        RegisteredUser user = userService.findByEmail(email);

        return getVersionByUserId(user.getId()).getVersion();
    }

    @Override
    public boolean isValidVersion(String email, Long tokenVersion) {
        try {
            return tokenVersion.equals(getCurrentVersion(email));
        } catch (ServiceException e) {
            log.warn("Failed to validate token version for user {}: {}", email, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isExists(Long userId) {
        return jwtVersionRepository.existsByUserId(userId);
    }

    private JwtVersion getVersionByUserId(Long userId) {
        return jwtVersionRepository.findByUserId(userId)
                .orElseThrow(() -> new ServiceException("Token version not found for user id: " + userId, HttpStatus.NOT_FOUND));
    }
}