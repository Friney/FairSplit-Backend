package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Paths.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public JwtAuthenticationDto login(@RequestBody UserCredentialsDto userCredentialsDto) {
        return authService.login(userCredentialsDto);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        return authService.refresh(refreshTokenDto);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody CreateRegisteredUserDto createRegisteredUserDto) {
        return authService.registration(createRegisteredUserDto);
    }
}
