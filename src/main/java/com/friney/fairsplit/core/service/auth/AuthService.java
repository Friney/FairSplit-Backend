package com.friney.fairsplit.core.service.auth;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserChangePasswordDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.api.dto.user.UserUpdateDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    JwtAuthenticationDto login(UserCredentialsDto user);

    UserDto registration(CreateRegisteredUserDto user);

    JwtAuthenticationDto refresh(RefreshTokenDto refreshTokenDto);

    void changePassword(UserChangePasswordDto userChangePasswordDto, UserDetails userDetails);

    UserDto update(UserUpdateDto userUpdateDto, UserDetails userDetails);

    void delete(UserDetails userDetails);

    UserDto loadUser(UserDetails userDetails);
}
