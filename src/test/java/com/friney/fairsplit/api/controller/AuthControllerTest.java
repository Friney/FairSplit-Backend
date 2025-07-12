package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserChangePasswordDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.api.dto.user.UserUpdateDto;
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
        UserCredentialsDto credentials = UserCredentialsDto.builder()
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
    void testRegister() {
        CreateRegisteredUserDto registrationDto = CreateRegisteredUserDto.builder()
                .name("controller")
                .email("controller@example.com")
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

    @Test
    void testChangePassword() {
        UserChangePasswordDto changePasswordDto = UserChangePasswordDto.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        UserDetails userDetails = createTestUserDetails();

        authController.changePassword(changePasswordDto, userDetails);

        verify(authService, times(1)).changePassword(changePasswordDto, userDetails);
    }

    @Test
    void testUpdate() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("newName")
                .build();
        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name("newName")
                .displayName("newName (controller@example.com)")
                .build();
        UserDetails userDetails = createTestUserDetails();

        when(authService.update(userUpdateDto, userDetails)).thenReturn(expectedUser);

        UserDto result = authController.update(userUpdateDto, userDetails);

        assertEquals(expectedUser, result);
        verify(authService, times(1)).update(userUpdateDto, userDetails);
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