package com.friney.fairsplit.core.service.user;

import com.friney.fairsplit.api.dto.user.UserCredentialsDto;
import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.user.NotRegisteredUser;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.entity.user.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.UserMapper;
import com.friney.fairsplit.core.repository.NotRegisteredUserRepository;
import com.friney.fairsplit.core.repository.RegisteredUserRepository;
import com.friney.fairsplit.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RegisteredUserRepository registeredUserRepository;
    private final NotRegisteredUserRepository notRegisteredUserRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAll() {
        return userMapper.map(userRepository.findAll());
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ServiceException("user with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public UserDto addRegisteredUser(CreateRegisteredUserDto userDto) {
        registeredUserRepository.findByEmail(userDto.email()).ifPresent(
                user -> {
                    throw new ServiceException("user with email " + userDto.email() + " already exists", HttpStatus.BAD_REQUEST);
                });
        RegisteredUser user = RegisteredUser.builder()
                .name(userDto.name())
                .email(userDto.email())
                .password(passwordEncoder.encode(userDto.password()))
                .build();
        registeredUserRepository.save(user);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    public UserDto addNotRegisteredUser(CreateNotRegisteredUserDto userDto) {
        notRegisteredUserRepository.findByName(userDto.name()).ifPresent(
                user -> {
                    throw new ServiceException("user with name " + userDto.name() + " already exists", HttpStatus.BAD_REQUEST);
                });
        NotRegisteredUser user = new NotRegisteredUser();
        user.setName(userDto.name());
        notRegisteredUserRepository.save(user);
        return userMapper.map(userRepository.save(user));
    }

    private RegisteredUser findByEmail(String email) {
        return registeredUserRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException("user with email " + email + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        RegisteredUser registeredUser = findByEmail(username);
        return org.springframework.security.core.userdetails.User.builder()
                .username(registeredUser.getEmail())
                .password(registeredUser.getPassword())
                .build();
    }

    @Override
    public RegisteredUser findByCredentials(UserCredentialsDto user) {
        RegisteredUser registeredUser = findByEmail(user.email());
        if (!passwordEncoder.matches(user.password(), registeredUser.getPassword())) {
            throw new ServiceException("wrong password", HttpStatus.UNAUTHORIZED);
        }
        return findByEmail(user.email());
    }

}
