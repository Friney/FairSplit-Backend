package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.User.NotRegisteredUserDto;
import com.friney.fairsplit.api.dto.User.RegisteredUserDto;
import com.friney.fairsplit.core.entity.User.NotRegisteredUser;
import com.friney.fairsplit.core.entity.User.RegisteredUser;
import com.friney.fairsplit.core.entity.User.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.repository.NotRegisteredUserRepository;
import com.friney.fairsplit.core.repository.RegisteredUserRepository;
import com.friney.fairsplit.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RegisteredUserRepository registeredUserRepository;
    private final NotRegisteredUserRepository notRegisteredUserRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ServiceException("User with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    public User create(RegisteredUserDto userDto) {
        Optional<RegisteredUser> existingUser = registeredUserRepository.findByEmail(userDto.email());
        if (existingUser.isPresent()) {
            throw new ServiceException("User with email " + userDto.email() + " already exists", HttpStatus.BAD_REQUEST);
        }
        RegisteredUser user = new RegisteredUser();
        user.setName(userDto.name());
        user.setEmail(userDto.email());
        return userRepository.save(user);
    }

    public User create(NotRegisteredUserDto userDto) {
        Optional<NotRegisteredUser> existingUser = notRegisteredUserRepository.findByName(userDto.name());
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        NotRegisteredUser user = new NotRegisteredUser();
        user.setName(userDto.name());
        return userRepository.save(user);
    }
}
