package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserChangePasswordDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.service.auth.AuthServiceImpl;
import com.friney.fairsplit.core.service.jwt.JwtService;
import com.friney.fairsplit.core.service.user.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testLogin() {
        UserCredentialsDto credentials = UserCredentialsDto.builder()
                .email("test@example.com")
                .password("password")
                .build();
        UserDetails userDetails = User.builder()
                .username(credentials.email())
                .password(credentials.password())
                .authorities(List.of())
                .build();
        JwtAuthenticationDto expectedToken = JwtAuthenticationDto.builder()
                .token("accessToken")
                .refreshToken("refreshToken")
                .build();

        when(userService.loadUserByUsername(credentials.email())).thenReturn(userDetails);
        when(jwtService.generateAuthToken(credentials.email())).thenReturn(expectedToken);

        JwtAuthenticationDto result = authService.login(credentials);

        assertEquals(expectedToken, result);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password()));
        verify(userService, times(1)).loadUserByUsername(credentials.email());
        verify(jwtService, times(1)).generateAuthToken(credentials.email());
    }

    @Test
    void testRegistration() {
        CreateRegisteredUserDto userDto = CreateRegisteredUserDto.builder()
                .name("user")
                .email("test@example.com")
                .password("password")
                .confirmPassword("password")
                .build();
        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name(userDto.email())
                .displayName(userDto.name() + " (" + userDto.email() + ")")
                .build();

        when(passwordEncoder.encode(userDto.password())).thenReturn("encodedPassword");
        when(userService.addRegisteredUser(any(RegisteredUser.class))).thenReturn(expectedUser);

        UserDto result = authService.registration(userDto);

        assertEquals(expectedUser, result);
        verify(passwordEncoder, times(1)).encode(userDto.password());
        verify(userService, times(1)).addRegisteredUser(any(RegisteredUser.class));
    }

    @Test
    void testRegistrationWhenPasswordsNotMatch() {
        CreateRegisteredUserDto userDto = CreateRegisteredUserDto.builder()
                .name("user")
                .email("test@example.com")
                .password("password")
                .confirmPassword("wrongPassword")
                .build();

        ServiceException exception = assertThrows(ServiceException.class,
                () -> authService.registration(userDto));

        assertEquals("passwords do not match", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void testRefresh() {
        String email = "test@example.com";
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken("refreshToken")
                .build();
        JwtAuthenticationDto expectedToken = JwtAuthenticationDto.builder()
                .token("newAccessToken")
                .refreshToken(refreshTokenDto.refreshToken())
                .build();

        when(jwtService.validateToken(refreshTokenDto.refreshToken())).thenReturn(true);
        when(jwtService.getEmailFromToken(refreshTokenDto.refreshToken())).thenReturn(email);
        when(jwtService.refreshBaseToken(email, refreshTokenDto.refreshToken())).thenReturn(expectedToken);

        JwtAuthenticationDto result = authService.refresh(refreshTokenDto);

        assertEquals(expectedToken, result);
        verify(jwtService, times(1)).validateToken(refreshTokenDto.refreshToken());
        verify(jwtService, times(1)).getEmailFromToken(refreshTokenDto.refreshToken());
        verify(jwtService, times(1)).refreshBaseToken(email, refreshTokenDto.refreshToken());
    }

    @Test
    void testRefreshWhenInvalidToken() {
        String invalidToken = "invalidToken";
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(invalidToken);

        when(jwtService.validateToken(invalidToken)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> authService.refresh(refreshTokenDto));

        assertEquals("invalid refresh token", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(jwtService).validateToken(invalidToken);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void testUpdatePassword() {
        UserChangePasswordDto updatePasswordDto = UserChangePasswordDto.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .confirmPassword("newPassword")
                .build();
        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name("user")
                .displayName("user" + " (test@example.com)")
                .build();
        UserDetails userDetails = createTestUserDetails();
        RegisteredUser registeredUser = RegisteredUser.builder()
                .email(expectedUser.name())
                .email(userDetails.getUsername())
                .password("oldPassword")
                .build();

        when(userService.findByEmail(userDetails.getUsername())).thenReturn(registeredUser);
        when(passwordEncoder.matches(updatePasswordDto.oldPassword(), registeredUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(updatePasswordDto.newPassword())).thenReturn("newPassword");
        when(userService.updateRegisteredUser(registeredUser)).thenReturn(expectedUser);

        authService.changePassword(updatePasswordDto, createTestUserDetails());
        registeredUser.setPassword("oldPassword");

        verify(userService, times(1)).findByEmail(userDetails.getUsername());
        verify(passwordEncoder, times(1)).matches(updatePasswordDto.oldPassword(), registeredUser.getPassword());
        verify(passwordEncoder, times(1)).encode(updatePasswordDto.newPassword());
        verify(userService, times(1)).updateRegisteredUser(registeredUser);
    }

    @Test
    void testUpdatePasswordWhenPasswordsNotMatch() {
        UserChangePasswordDto updatePasswordDto = UserChangePasswordDto.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .confirmPassword("wrongPassword")
                .build();

        UserDetails userDetails = createTestUserDetails();

        ServiceException exception = assertThrows(ServiceException.class,
                () -> authService.changePassword(updatePasswordDto, userDetails));

        assertEquals("passwords do not match", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void testUpdatePasswordWhenOldPasswordNotMatch() {
        UserChangePasswordDto updatePasswordDto = UserChangePasswordDto.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .confirmPassword("newPassword")
                .build();

        UserDetails userDetails = createTestUserDetails();

        RegisteredUser registeredUser = RegisteredUser.builder()
                .email(userDetails.getUsername())
                .password("encodedPassword")
                .build();

        when(userService.findByEmail(userDetails.getUsername())).thenReturn(registeredUser);
        when(passwordEncoder.matches(updatePasswordDto.oldPassword(), registeredUser.getPassword())).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> authService.changePassword(updatePasswordDto, userDetails));

        assertEquals("old password is incorrect", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void testDelete() {
        RegisteredUser registeredUser = RegisteredUser.builder()
                .email("email")
                .build();
        UserDetails userDetails = createTestUserDetails();

        when(userService.findByEmail(userDetails.getUsername())).thenReturn(registeredUser);

        authService.delete(userDetails);
        verify(userService, times(1)).deleteRegisteredUser(registeredUser);
    }

    private UserDetails createTestUserDetails() {
        return User.builder()
                .username("username")
                .password("password")
                .authorities(List.of())
                .build();
    }
}