package com.friney.fairsplit.core.service.auth;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserChangePasswordDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.UserUpdateDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    JwtAuthenticationDto login(UserCredentialsDto user);

    RegisteredUserDto registration(CreateRegisteredUserDto user);

    JwtAuthenticationDto refresh(RefreshTokenDto refreshTokenDto);

    void changePassword(UserChangePasswordDto userChangePasswordDto, UserDetails userDetails);

    RegisteredUserDto update(UserUpdateDto userUpdateDto, UserDetails userDetails);

    void delete(UserDetails userDetails);

    RegisteredUserDto loadUser(UserDetails userDetails);
}
