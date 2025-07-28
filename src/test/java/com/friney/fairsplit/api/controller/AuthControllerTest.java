package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenRequest;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserRequest;
import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserChangePasswordRequest;
import com.friney.fairsplit.api.dto.user.UserCredentialsRequest;
import com.friney.fairsplit.api.dto.user.UserUpdateRequest;
import com.friney.fairsplit.core.service.auth.AuthService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
    void testLogin() {
        UserCredentialsRequest credentials = UserCredentialsRequest.builder()
                .email("controller@example.com")
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
    void testRefresh() {
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();
        JwtAuthenticationDto expectedResponse = JwtAuthenticationDto.builder()
                .token("newAccessToken")
                .refreshToken("refreshToken")
                .build();

        when(authService.refresh(refreshTokenRequest)).thenReturn(expectedResponse);

        JwtAuthenticationDto result = authController.refresh(refreshTokenRequest);

        assertEquals(expectedResponse, result);
        verify(authService, times(1)).refresh(refreshTokenRequest);
    }

    @Test
    void testRegister() {
        CreateRegisteredUserRequest registrationDto = CreateRegisteredUserRequest.builder()
                .name("controller")
                .email("controller@example.com")
                .password("password")
                .confirmPassword("password")
                .build();
        RegisteredUserDto expectedUser = RegisteredUserDto.builder()
                .id(1L)
                .email(registrationDto.email())
                .name(registrationDto.name())
                .displayName(registrationDto.name() + " (" + registrationDto.email() + ")")
                .build();


        when(authService.registration(registrationDto)).thenReturn(expectedUser);

        RegisteredUserDto result = authController.register(registrationDto);

        assertEquals(expectedUser, result);
        verify(authService, times(1)).registration(registrationDto);
    }

    @Test
    void testChangePassword() {
        UserChangePasswordRequest changePasswordDto = UserChangePasswordRequest.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        UserDetails userDetails = createTestUserDetails();

        authController.changePassword(changePasswordDto, userDetails);

        verify(authService, times(1)).changePassword(changePasswordDto, userDetails);
    }

    @Test
    void testUpdate() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .name("newName")
                .build();
        RegisteredUserDto expectedUser = RegisteredUserDto.builder()
                .id(1L)
                .email("controller@example.com")
                .name("newName")
                .displayName("newName (controller@example.com)")
                .build();
        UserDetails userDetails = createTestUserDetails();

        when(authService.update(userUpdateRequest, userDetails)).thenReturn(expectedUser);

        RegisteredUserDto result = authController.update(userUpdateRequest, userDetails);

        assertEquals(expectedUser, result);
        verify(authService, times(1)).update(userUpdateRequest, userDetails);
    }

    @Test
    void testDelete() {
        UserDetails userDetails = createTestUserDetails();

        authController.delete(userDetails);

        verify(authService, times(1)).delete(userDetails);
    }

    private UserDetails createTestUserDetails() {
        return User.builder()
                .username("username")
                .password("password")
                .authorities(List.of())
                .build();
    }
}