package com.friney.fairsplit.core.service.auth;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.UserDto;

public interface AuthService {

    JwtAuthenticationDto login(UserCredentialsDto user);

    UserDto registration(CreateRegisteredUserDto user);

    JwtAuthenticationDto refresh(RefreshTokenDto refreshTokenDto);

}
