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
        RegisteredUser user = new RegisteredUser();
        user.setName(userDto.name());
        user.setEmail(userDto.email());
        registeredUserRepository.save(user);
        return userRepository.save(user);
    }

    public User create(NotRegisteredUserDto userDto) {
        NotRegisteredUser user = new NotRegisteredUser();
        user.setName(userDto.name());
        notRegisteredUserRepository.save(user);
        return userRepository.save(user);
    }
}
