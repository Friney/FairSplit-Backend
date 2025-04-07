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
import com.friney.fairsplit.core.service.user.UserService;
import com.friney.fairsplit.core.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

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
                .name("User 1")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("User 2")
                .build();

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAll();

        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = userService.getAll();

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        User user = User.builder()
                .id(1L)
                .name("User")
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

        assertEquals("User with id 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateRegisteredUser() {
        RegisteredUserDto userDto = RegisteredUserDto.builder()
                .name("User")
                .email("example@example.com")
                .build();
        RegisteredUser savedUser = RegisteredUser.builder().
                id(1L).
                name(userDto.name()).
                email(userDto.email()).
                build();

        when(registeredUserRepository.save(any(RegisteredUser.class))).thenReturn(savedUser);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.create(userDto);

        assertEquals(savedUser, result);
        verify(registeredUserRepository, times(1)).save(any(RegisteredUser.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateNotRegisteredUser() {
        NotRegisteredUserDto userDto = NotRegisteredUserDto.builder()
                .name("User")
                .build();
        NotRegisteredUser savedUser = NotRegisteredUser.builder()
                .id(1L)
                .name(userDto.name())
                .build();

        when(notRegisteredUserRepository.save(any(NotRegisteredUser.class))).thenReturn(savedUser);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.create(userDto);

        assertEquals(savedUser, result);
        verify(notRegisteredUserRepository, times(1)).save(any(NotRegisteredUser.class));
        verify(userRepository, times(1)).save(any(User.class));
    }
} 