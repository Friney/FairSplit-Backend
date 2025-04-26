package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.user.NotRegisteredUser;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.service.user.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createRegisteredUser() {
        CreateRegisteredUserDto registeredUser = CreateRegisteredUserDto.builder()
                .name("user")
                .email("example@example.com")
                .build();

        RegisteredUser user = RegisteredUser.builder()
                .name(registeredUser.name())
                .email(registeredUser.email())
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .displayName(user.getName() + " (example@example.com)")
                .build();

        when(userService.addRegisteredUser(registeredUser)).thenReturn(expectedUser);

        UserDto result = userController.create(registeredUser);

        assertEquals(result, expectedUser);
        verify(userService, times(1)).addRegisteredUser(registeredUser);
    }

    @Test
    void testCreateNotRegisteredUser() {
        CreateNotRegisteredUserDto notRegisteredUser = CreateNotRegisteredUserDto.builder()
                .name("user")
                .build();

        NotRegisteredUser user = NotRegisteredUser.builder()
                .name(notRegisteredUser.name())
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .displayName(user.getName())
                .build();

        when(userService.addNotRegisteredUser(notRegisteredUser)).thenReturn(expectedUser);

        UserDto result = userController.create(notRegisteredUser);

        assertEquals(result, expectedUser);
        verify(userService, times(1)).addNotRegisteredUser(notRegisteredUser);
    }

    @Test
    void getAll() {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("user 1")
                .displayName("user 1")
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("user 2")
                .displayName("user 2 (example@example.com)")
                .build();

        List<UserDto> expectedUsers = List.of(user1, user2);

        when(userService.getAll()).thenReturn(expectedUsers);

        List<UserDto> result = userController.getAll();

        assertEquals(expectedUsers, result);
        verify(userService, times(1)).getAll();
    }
}
