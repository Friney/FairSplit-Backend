package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login() {
        UserCredentialsDto credentials = UserCredentialsDto.builder()
                .email("user@example.com")
                .password("password")
                .build();
        JwtAuthenticationDto expectedResponse = JwtAuthenticationDto.builder()
                .token("accessToken")
                .refreshToken("refreshToken")
                .build();

        when(authService.login(credentials)).thenReturn(expectedResponse);

        JwtAuthenticationDto result = authController.login(credentials);

        assertEquals(expectedResponse, result);
        verify(authService, times(1)).login(credentials);
    }

    @Test
    void refresh() {
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken("refreshToken")
                .build();
        JwtAuthenticationDto expectedResponse = JwtAuthenticationDto.builder()
                .token("newAccessToken")
                .refreshToken("refreshToken")
                .build();

        when(authService.refresh(refreshTokenDto)).thenReturn(expectedResponse);

        JwtAuthenticationDto result = authController.refresh(refreshTokenDto);

        assertEquals(expectedResponse, result);
        verify(authService, times(1)).refresh(refreshTokenDto);
    }

    @Test
    void register() {
        CreateRegisteredUserDto registrationDto = CreateRegisteredUserDto.builder()
                .name("user")
                .email("user@example.com")
                .password("password")
                .confirmPassword("password")
                .build();
        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name(registrationDto.name())
                .displayName(registrationDto.name() + " (" + registrationDto.email() + ")")
                .build();

        when(authService.registration(registrationDto)).thenReturn(expectedUser);

        UserDto result = authController.register(registrationDto);

        assertEquals(expectedUser, result);
        verify(authService, times(1)).registration(registrationDto);
    }

}