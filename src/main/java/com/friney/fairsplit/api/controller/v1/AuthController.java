package com.friney.fairsplit.api.controller.v1;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenRequest;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserRequest;
import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserChangePasswordRequest;
import com.friney.fairsplit.api.dto.user.UserCredentialsRequest;
import com.friney.fairsplit.api.dto.user.UserUpdateRequest;
import com.friney.fairsplit.core.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Paths.AUTH_V1)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public JwtAuthenticationDto login(@Valid @RequestBody UserCredentialsRequest userCredentialsRequest) {
        return authService.login(userCredentialsRequest);
    }

    @GetMapping("/me")
    public RegisteredUserDto me(@Valid @AuthenticationPrincipal UserDetails userDetails) {
        return authService.loadUser(userDetails);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refresh(refreshTokenRequest);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisteredUserDto register(@Valid @RequestBody CreateRegisteredUserRequest createRegisteredUserRequest) {
        return authService.registration(createRegisteredUserRequest);
    }

    @PatchMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody UserChangePasswordRequest userChangePasswordRequest, @AuthenticationPrincipal UserDetails userDetails) {
        authService.changePassword(userChangePasswordRequest, userDetails);
    }

    @PatchMapping
    public RegisteredUserDto update(@Valid @RequestBody UserUpdateRequest userUpdateRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return authService.update(userUpdateRequest, userDetails);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserDetails userDetails) {
        authService.delete(userDetails);
    }
}
