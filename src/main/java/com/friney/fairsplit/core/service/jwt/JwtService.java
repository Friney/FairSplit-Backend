package com.friney.fairsplit.core.service.jwt;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import java.security.Key;
import java.time.Duration;
import javax.crypto.SecretKey;

public interface JwtService {

    JwtAuthenticationDto generateAuthToken(String email);

    JwtAuthenticationDto refreshBaseToken(String email, String refreshToken);

    String getEmailFromToken(String token);

    boolean validateToken(String token);

    String generateJwtToken(String email);


    String generateRefreshToken(String email);

    String generateToken(String email, Duration duration);

    SecretKey getSingInKey();
}
