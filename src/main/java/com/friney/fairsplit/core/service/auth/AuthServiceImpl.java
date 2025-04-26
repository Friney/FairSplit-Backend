package com.friney.fairsplit.core.service.auth;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.service.jwt.JwtService;
import com.friney.fairsplit.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public JwtAuthenticationDto signIn(UserCredentialsDto user) {
        RegisteredUser registeredUser = userService.findByCredentials(user);
        return jwtService.generateAuthToken(registeredUser.getEmail());
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
