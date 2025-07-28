package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenRequest;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserRequest;
import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserChangePasswordRequest;
import com.friney.fairsplit.api.dto.user.UserCredentialsRequest;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.service.auth.AuthServiceImpl;
import com.friney.fairsplit.core.service.jwt.JwtService;
import com.friney.fairsplit.core.service.jwt.version.JwtVersionService;
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
import static org.mockito.Mockito.doNothing;
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
    @Mock
    private JwtVersionService jwtVersionService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testLogin() {
        UserCredentialsRequest credentials = UserCredentialsRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();
        RegisteredUser user = RegisteredUser.builder()
                .id(1L)
                .email(credentials.email())
                .password("encodedPassword")
                .build();
        JwtAuthenticationDto expectedToken = JwtAuthenticationDto.builder()
                .token("accessToken")
                .refreshToken("refreshToken")
                .build();

        when(userService.findByEmail(credentials.email())).thenReturn(user);
        when(jwtService.generateAuthToken(credentials.email())).thenReturn(expectedToken);
        when(jwtVersionService.isExists(user.getId())).thenReturn(true);

        JwtAuthenticationDto result = authService.login(credentials);

        assertEquals(expectedToken, result);
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password()));
        verify(userService, times(1)).findByEmail(credentials.email());
        verify(jwtService, times(1)).generateAuthToken(credentials.email());
    }

    @Test
    void testRegistration() {
        CreateRegisteredUserRequest userDto = CreateRegisteredUserRequest.builder()
                .name("controller")
                .email("test@example.com")
                .password("password")
                .confirmPassword("password")
                .build();
        RegisteredUserDto expectedUser = RegisteredUserDto.builder()
                .id(1L)
                .email(userDto.email())
                .name(userDto.email())
                .displayName(userDto.name() + " (" + userDto.email() + ")")
                .build();

        when(passwordEncoder.encode(userDto.password())).thenReturn("encodedPassword");
        when(userService.addRegisteredUser(any(RegisteredUser.class))).thenReturn(expectedUser);
        doNothing().when(jwtVersionService).createInitialVersion(expectedUser.id());

        RegisteredUserDto result = authService.registration(userDto);

        assertEquals(expectedUser, result);
        verify(passwordEncoder, times(1)).encode(userDto.password());
        verify(userService, times(1)).addRegisteredUser(any(RegisteredUser.class));
    }

    @Test
    void testRegistrationWhenPasswordsNotMatch() {
        CreateRegisteredUserRequest userDto = CreateRegisteredUserRequest.builder()
                .name("controller")
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
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();

        JwtAuthenticationDto expectedToken = JwtAuthenticationDto.builder()
                .token("newAccessToken")
                .refreshToken(refreshTokenRequest.refreshToken())
                .build();

        RegisteredUser expectedUser = RegisteredUser.builder()
                .id(1L)
                .email(email)
                .build();

        when(jwtService.validateToken(refreshTokenRequest.refreshToken())).thenReturn(true);
        when(jwtService.getEmailFromToken(refreshTokenRequest.refreshToken())).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(expectedUser);
        when(jwtService.refreshTokens(email, refreshTokenRequest.refreshToken())).thenReturn(expectedToken);
        doNothing().when(jwtVersionService).incrementVersion(expectedUser.getId());

        JwtAuthenticationDto result = authService.refresh(refreshTokenRequest);

        assertEquals(expectedToken, result);
        verify(jwtService, times(1)).validateToken(refreshTokenRequest.refreshToken());
        verify(jwtService, times(1)).getEmailFromToken(refreshTokenRequest.refreshToken());
        verify(jwtService, times(1)).refreshTokens(email, refreshTokenRequest.refreshToken());
    }

    @Test
    void testRefreshWhenInvalidToken() {
        String invalidToken = "invalidToken";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(invalidToken);

        when(jwtService.validateToken(invalidToken)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> authService.refresh(refreshTokenRequest));

        assertEquals("invalid refresh token", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(jwtService).validateToken(invalidToken);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void testUpdatePassword() {
        UserChangePasswordRequest updatePasswordDto = UserChangePasswordRequest.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .confirmPassword("newPassword")
                .build();

        RegisteredUserDto expectedUser = RegisteredUserDto.builder()
                .id(1L)
                .email("test@example.com")
                .name("controller")
                .displayName("controller" + " (test@example.com)")
                .build();

        UserDetails userDetails = createTestUserDetails();
        RegisteredUser registeredUser = RegisteredUser.builder()
                .id(expectedUser.id())
                .name(expectedUser.name())
                .email(expectedUser.email())
                .password("oldPassword")
                .build();

        when(userService.findByEmail(userDetails.getUsername())).thenReturn(registeredUser);
        when(passwordEncoder.matches(updatePasswordDto.oldPassword(), registeredUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(updatePasswordDto.newPassword())).thenReturn("newPassword");
        when(userService.updateRegisteredUser(registeredUser)).thenReturn(expectedUser);
        doNothing().when(jwtVersionService).incrementVersion(registeredUser.getId());

        authService.changePassword(updatePasswordDto, createTestUserDetails());
        registeredUser.setPassword("oldPassword");

        verify(userService, times(1)).findByEmail(userDetails.getUsername());
        verify(passwordEncoder, times(1)).matches(updatePasswordDto.oldPassword(), registeredUser.getPassword());
        verify(passwordEncoder, times(1)).encode(updatePasswordDto.newPassword());
        verify(userService, times(1)).updateRegisteredUser(registeredUser);
    }

    @Test
    void testUpdatePasswordWhenPasswordsNotMatch() {
        UserChangePasswordRequest updatePasswordDto = UserChangePasswordRequest.builder()
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
        UserChangePasswordRequest updatePasswordDto = UserChangePasswordRequest.builder()
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