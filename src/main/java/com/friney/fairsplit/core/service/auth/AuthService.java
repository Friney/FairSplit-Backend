package com.friney.fairsplit.core.service.auth;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;

public interface AuthService {

    JwtAuthenticationDto signIn(UserCredentialsDto user);

    JwtAuthenticationDto refresh(RefreshTokenDto refreshTokenDto);
}
