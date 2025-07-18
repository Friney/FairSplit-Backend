package com.friney.fairsplit.core.service;

import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserDto;
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
import com.friney.fairsplit.core.service.user.UserServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RegisteredUserRepository registeredUserRepository;

    @Mock
    private NotRegisteredUserRepository notRegisteredUserRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetAll() {
        User user1 = User.builder()
                .id(1L)
                .name("controller 1")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("controller 2")
                .build();

        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("controller 1")
                .displayName("controller 1")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("controller 2")
                .displayName("controller 2")
                .build();

        List<User> users = Arrays.asList(user1, user2);

        List<UserDto> expectedUsers = Arrays.asList(userDto1, userDto2);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.map(users)).thenReturn(expectedUsers);

        List<UserDto> result = userService.getAll();

        assertEquals(expectedUsers, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAll();

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        User user = User.builder()
                .id(1L)
                .name("controller")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.getById(1L));

        assertEquals("user with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testAddRegisteredUserRegisteredUser() {
        RegisteredUser savedUser = RegisteredUser.builder()
                .id(1L)
                .name("controller")
                .email("example@example.com")
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .displayName(savedUser.getName() + " (example@example.com)")
                .build();

        when(registeredUserRepository.save(any(RegisteredUser.class))).thenReturn(savedUser);
        when(userMapper.map(any(User.class))).thenReturn(expectedUser);

        UserDto result = userService.addRegisteredUser(savedUser);

        assertEquals(expectedUser, result);
        verify(registeredUserRepository, times(1)).save(any(RegisteredUser.class));
    }

    @Test
    void testAddNotRegisteredUser() {
        CreateNotRegisteredUserDto userDto = CreateNotRegisteredUserDto.builder()
                .name("controller")
                .build();
        NotRegisteredUser savedUser = NotRegisteredUser.builder()
                .id(1L)
                .name(userDto.name())
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .displayName(savedUser.getName())
                .build();

        when(notRegisteredUserRepository.save(any(NotRegisteredUser.class))).thenReturn(savedUser);
        when(userMapper.map(any(User.class))).thenReturn(expectedUser);

        UserDto result = userService.addNotRegisteredUser(userDto);

        assertEquals(expectedUser, result);
        verify(notRegisteredUserRepository, times(1)).save(any(NotRegisteredUser.class));
    }

    @Test
    void loadUserByUsername() {
        String email = "example@example.com";
        RegisteredUser savedUser = RegisteredUser.builder()
                .id(1L)
                .name("controller")
                .email(email)
                .password("password")
                .build();

        when(registeredUserRepository.findByEmail(email)).thenReturn(Optional.of(savedUser));

        UserDetails result = userService.loadUserByUsername(email);

        assertEquals(savedUser.getEmail(), result.getUsername());
        assertEquals(savedUser.getPassword(), result.getPassword());
        verify(registeredUserRepository, times(1)).findByEmail(email);
    }

    @Test
    void testUpdateRegisteredUser() {
        RegisteredUser user = RegisteredUser.builder()
                .id(1L)
                .name("controller")
                .email("example@example.com")
                .password("password")
                .build();

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .displayName(user.getName() + " (example@example.com)")
                .build();

        when(registeredUserRepository.save(any(RegisteredUser.class))).thenReturn(user);
        when(userMapper.map(any(User.class))).thenReturn(userDto);

        UserDto result = userService.updateRegisteredUser(user);

        assertEquals(userDto, result);
        verify(registeredUserRepository, times(1)).save(any(RegisteredUser.class));
    }

    @Test
    void testDeleteRegisteredUser() {
        RegisteredUser user = RegisteredUser.builder()
                .id(1L)
                .name("controller")
                .email("example@example.com")
                .password("password")
                .build();

        doNothing().when(registeredUserRepository).delete(user);

        userService.deleteRegisteredUser(user);

        verify(registeredUserRepository, times(1)).delete(user);
    }

    @Test
    void testUpdateNotRegisteredUser() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("controller")
                .build();

        NotRegisteredUser user = NotRegisteredUser.builder()
                .id(1L)
                .name(userUpdateDto.name())
                .build();

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .displayName(user.getName())
                .build();

        when(notRegisteredUserRepository.save(any(NotRegisteredUser.class))).thenReturn(user);
        when(userMapper.map(any(User.class))).thenReturn(userDto);

        UserDto result = userService.updateNotRegisteredUser(userUpdateDto, 1L);

        assertEquals(userDto, result);
        verify(notRegisteredUserRepository, times(1)).save(any(NotRegisteredUser.class));
    }
} 