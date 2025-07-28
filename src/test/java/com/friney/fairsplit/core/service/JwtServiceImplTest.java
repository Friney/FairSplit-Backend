package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.core.service.jwt.JwtServiceImpl;
import com.friney.fairsplit.core.service.jwt.version.JwtVersionService;
import java.time.Duration;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final String TEST_SECRET = "testSecretKey123456789012345678901234567890";
    private static final String TEST_EMAIL = "test@example.com";
    private static final Duration TEST_LIFETIME = Duration.ofMinutes(30);

    @Mock
    private JwtVersionService jwtVersionService;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtLifetime", TEST_LIFETIME);
    }

    @Test
    void generateAuthToken() {
        JwtAuthenticationDto result = jwtService.generateAuthToken(TEST_EMAIL);

        assertNotNull(result);
        assertNotNull(result.token());
        assertNotNull(result.refreshToken());
        assertNotEquals(result.token(), result.refreshToken());
    }

    @Test
    void refreshTokens() {
        String refreshToken = "existingRefreshToken";
        when(jwtVersionService.getCurrentVersion(TEST_EMAIL)).thenReturn(1L);
        JwtAuthenticationDto result = jwtService.refreshTokens(TEST_EMAIL, refreshToken);

        assertNotNull(result);
        assertNotNull(result.token());
        assertNotEquals(refreshToken, result.refreshToken());
    }

    @Test
    void getEmailFromToken() {
        String token = jwtService.generateJwtToken(TEST_EMAIL);
        String email = jwtService.getEmailFromToken(token);

        assertEquals(TEST_EMAIL, email);
    }

    @Test
    void validateTokenWithValidToken() {
        when(jwtVersionService.getCurrentVersion(TEST_EMAIL)).thenReturn(1L);
        when(jwtVersionService.isValidVersion(TEST_EMAIL, 1L)).thenReturn(true);

        String token = jwtService.generateJwtToken(TEST_EMAIL);

        boolean isValid = jwtService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateTokenWithInvalidToken() {
        boolean isValid = jwtService.validateToken("invalid.token.here");

        assertFalse(isValid);
    }

    @Test
    void generateJwtToken() {
        String token = jwtService.generateJwtToken(TEST_EMAIL);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateRefreshToken() {
        String token = jwtService.generateRefreshToken(TEST_EMAIL);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken() {
        String token = jwtService.generateToken(TEST_EMAIL, Duration.ofHours(1));

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getSingInKey() {
        SecretKey key = jwtService.getSingInKey();

        assertNotNull(key);
        assertEquals("HmacSHA256", key.getAlgorithm());
    }

}