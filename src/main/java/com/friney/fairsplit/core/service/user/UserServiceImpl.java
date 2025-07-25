package com.friney.fairsplit.core.service.user;

import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.api.dto.user.UserUpdateDto;
import com.friney.fairsplit.core.entity.user.NotRegisteredUser;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.entity.user.User;
import com.friney.fairsplit.core.exception.ServiceException;
import com.friney.fairsplit.core.mapper.UserMapper;
import com.friney.fairsplit.core.repository.NotRegisteredUserRepository;
import com.friney.fairsplit.core.repository.RegisteredUserRepository;
import com.friney.fairsplit.core.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@SuppressWarnings("java:S1135")  // Suppress "TODO" comment warning
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RegisteredUserRepository registeredUserRepository;
    private final NotRegisteredUserRepository notRegisteredUserRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        Sort sort = Sort.sort(User.class).by(User::getName);
        return userMapper.mapUser(userRepository.findAll(sort));
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ServiceException("user with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public RegisteredUserDto addRegisteredUser(RegisteredUser user) {
        registeredUserRepository.findByEmail(user.getEmail()).ifPresent(
                u -> {
                    throw new ServiceException("user with email " + u.getEmail() + " already exists", HttpStatus.BAD_REQUEST);
                });

        return userMapper.mapRegisteredUser(registeredUserRepository.save(user));
    }

    @Override
    public UserDto addNotRegisteredUser(CreateNotRegisteredUserDto userDto) {
        return notRegisteredUserRepository.findByName(userDto.name())
                .map(userMapper::mapUser)
                .orElseGet(() -> {
                            NotRegisteredUser user = NotRegisteredUser.builder()
                                    .name(userDto.name())
                                    .build();

                            return userMapper.mapUser(notRegisteredUserRepository.save(user));
                        }
                );
    }

    @Override
    public RegisteredUser findByEmail(String email) {
        return registeredUserRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException("user with email " + email + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public RegisteredUserDto findRegisteredDtoByEmail(String username) {
        return userMapper.mapRegisteredUser(findByEmail(username));
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
    public RegisteredUserDto updateRegisteredUser(RegisteredUser user) {
        return userMapper.mapRegisteredUser(registeredUserRepository.save(user));
    }

    @Override
    public void deleteRegisteredUser(RegisteredUser registeredUser) {
        registeredUserRepository.delete(registeredUser);
    }

    @Override
    public UserDto updateNotRegisteredUser(UserUpdateDto userUpdateDto, Long id) {
        // TODO Сделать проверку использования юзера, если не где не используется, то обновить
        CreateNotRegisteredUserDto createNotRegisteredUserDto = CreateNotRegisteredUserDto.builder()
                .name(userUpdateDto.name())
                .build();
        return addNotRegisteredUser(createNotRegisteredUserDto);
    }

    @Override
    public void deleteNotRegisteredUser(Long id) {
        // TODO Сделать проверку использования юзера, если не где не используется, то удалить
    }
}
