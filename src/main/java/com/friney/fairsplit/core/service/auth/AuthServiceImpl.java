package com.friney.fairsplit.core.service.auth;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.service.jwt.JwtService;
import com.friney.fairsplit.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationDto login(UserCredentialsDto userDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password()));
        UserDetails user = userService.loadUserByUsername(userDto.email());

        return jwtService.generateAuthToken(user.getUsername());
    }

    @Override
    public UserDto registration(CreateRegisteredUserDto userDto) {
        if (!userDto.password().equals(userDto.confirmPassword())) {
            throw new ServiceException("passwords do not match", HttpStatus.BAD_REQUEST);
        }

        RegisteredUser registeredUser = RegisteredUser.builder()
                .email(userDto.email())
                .name(userDto.name())
                .password(passwordEncoder.encode(userDto.password()))
                .build();

        return userService.addRegisteredUser(registeredUser);
    }

    @Override
    public JwtAuthenticationDto refresh(RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.refreshToken();
        if (refreshToken == null || !jwtService.validateToken(refreshToken)) {
            throw new ServiceException("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
        return jwtService.refreshBaseToken(jwtService.getEmailFromToken(refreshToken), refreshToken);
    }

}
