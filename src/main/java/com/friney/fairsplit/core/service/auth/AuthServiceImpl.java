package com.friney.fairsplit.core.service.auth;

import com.friney.fairsplit.api.dto.jwt.JwtAuthenticationDto;
import com.friney.fairsplit.api.dto.jwt.RefreshTokenDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserChangePasswordDto;
import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.UserUpdateDto;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.service.jwt.JwtService;
import com.friney.fairsplit.core.service.jwt.version.JwtVersionService;
import com.friney.fairsplit.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtVersionService jwtVersionService;

    @Override
    public JwtAuthenticationDto login(UserCredentialsDto userDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password()));
        RegisteredUser user = userService.findByEmail(userDto.email());
        if (!jwtVersionService.isExists(user.getId())) {
            jwtVersionService.createInitialVersion(user.getId());
        }
        return jwtService.generateAuthToken(user.getEmail());
    }

    @Override
    @Transactional
    public RegisteredUserDto registration(CreateRegisteredUserDto userDto) {
        if (!userDto.password().equals(userDto.confirmPassword())) {
            throw new ServiceException("passwords do not match", HttpStatus.BAD_REQUEST);
        }

        RegisteredUser registeredUser = RegisteredUser.builder()
                .email(userDto.email())
                .name(userDto.name())
                .password(passwordEncoder.encode(userDto.password()))
                .build();

        RegisteredUserDto user = userService.addRegisteredUser(registeredUser);
        jwtVersionService.createInitialVersion(user.id());

        return user;
    }

    @Override
    public JwtAuthenticationDto refresh(RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.refreshToken();
        if (refreshToken == null || !jwtService.validateToken(refreshToken)) {
            throw new ServiceException("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String email = jwtService.getEmailFromToken(refreshToken);
        Long userId = userService.findByEmail(email).getId();
        jwtVersionService.incrementVersion(userId);

        return jwtService.refreshTokens(email, refreshToken);
    }

    @Override
    @Transactional
    public void changePassword(UserChangePasswordDto userChangePasswordDto, UserDetails userDetails) {
        if (!userChangePasswordDto.newPassword().equals(userChangePasswordDto.confirmPassword())) {
            throw new ServiceException("passwords do not match", HttpStatus.BAD_REQUEST);
        }
        RegisteredUser registeredUser = userService.findByEmail(userDetails.getUsername());
        if (!passwordEncoder.matches(userChangePasswordDto.oldPassword(), registeredUser.getPassword())) {
            throw new ServiceException("old password is incorrect", HttpStatus.BAD_REQUEST);
        }
        registeredUser.setPassword(passwordEncoder.encode(userChangePasswordDto.newPassword()));

        jwtVersionService.incrementVersion(registeredUser.getId());

        userService.updateRegisteredUser(registeredUser);
    }

    @Override
    public RegisteredUserDto loadUser(UserDetails userDetails) {
        return userService.findRegisteredDtoByEmail(userDetails.getUsername());
    }

    @Override
    @Transactional
    public RegisteredUserDto update(UserUpdateDto userUpdateDto, UserDetails userDetails) {
        RegisteredUser registeredUser = userService.findByEmail(userDetails.getUsername());
        if (userUpdateDto.name() != null) {
            registeredUser.setName(userUpdateDto.name());
        }

        jwtVersionService.incrementVersion(registeredUser.getId());

        return userService.updateRegisteredUser(registeredUser);
    }

    @Override
    public void delete(UserDetails userDetails) {
        RegisteredUser registeredUser = userService.findByEmail(userDetails.getUsername());
        userService.deleteRegisteredUser(registeredUser);
    }
}
