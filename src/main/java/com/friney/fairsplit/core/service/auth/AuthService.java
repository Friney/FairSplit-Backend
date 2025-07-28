package com.friney.fairsplit.core.service.auth;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenRequest;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserRequest;
import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserChangePasswordRequest;
import com.friney.fairsplit.api.dto.user.UserCredentialsRequest;
import com.friney.fairsplit.api.dto.user.UserUpdateRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    JwtAuthenticationDto login(UserCredentialsRequest user);

    RegisteredUserDto registration(CreateRegisteredUserRequest user);

    JwtAuthenticationDto refresh(RefreshTokenRequest refreshTokenRequest);

    void changePassword(UserChangePasswordRequest userChangePasswordRequest, UserDetails userDetails);

    RegisteredUserDto update(UserUpdateRequest userUpdateRequest, UserDetails userDetails);

    void delete(UserDetails userDetails);

    RegisteredUserDto loadUser(UserDetails userDetails);
}
