package com.friney.fairsplit.core.service.jwt;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.core.service.jwt.version.JwtVersionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token-lifetime}")
    private Duration jwtLifetime;

    @Value("${jwt.refresh-lifetime}")
    private final Duration refreshTokenLifetime = Duration.ofDays(7);

    private final JwtVersionService jwtVersionService;

    @Override
    public JwtAuthenticationDto generateAuthToken(String email) {
        return JwtAuthenticationDto.builder()
                .token(generateJwtToken(email))
                .refreshToken(generateRefreshToken(email))
                .build();
    }

    @Override
    public JwtAuthenticationDto refreshTokens(String email, String refreshToken) {
        return JwtAuthenticationDto.builder()
                .token(generateJwtToken(email))
                .refreshToken(generateRefreshToken(email))
                .build();
    }

    @Override
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSingInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            Long version = claims.get("version", Long.class);
            return jwtVersionService.isValidVersion(email, version);
        } catch (Exception e) {
            log.info("{} -> {}", e.getClass(), e.getMessage());
            return false;
        }
    }

    @Override
    public String generateJwtToken(String email) {
        return generateToken(email, jwtLifetime);
    }

    @Override
    public String generateRefreshToken(String email) {
        return generateToken(email, refreshTokenLifetime);
    }

    @Override
    public String generateToken(String email, Duration duration) {
        Date date = Date.from(LocalDateTime.now().plus(duration).atZone(ZoneId.systemDefault()).toInstant());
        Long version = jwtVersionService.getCurrentVersion(email);
        return Jwts.builder()
                .subject(email)
                .claim("version", version)
                .expiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    @Override
    public SecretKey getSingInKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
