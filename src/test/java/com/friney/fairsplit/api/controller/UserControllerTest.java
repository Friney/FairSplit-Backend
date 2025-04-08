package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.user.NotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.core.entity.user.NotRegisteredUser;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.entity.user.User;
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
        RegisteredUserDto registeredUser = RegisteredUserDto.builder()
                .name("user")
                .email("example@example.com")
                .build();

        RegisteredUser user = RegisteredUser.builder()
                .name(registeredUser.name())
                .email(registeredUser.email())
                .build();

        when(userService.create(registeredUser)).thenReturn(user);

        RegisteredUser result = (RegisteredUser) userController.create(registeredUser);

        assertEquals(user, result);
        verify(userService, times(1)).create(registeredUser);
    }

    @Test
    void testCreateNotRegisteredUser() {
        NotRegisteredUserDto notRegisteredUser = NotRegisteredUserDto.builder()
                .name("user")
                .build();

        NotRegisteredUser user = NotRegisteredUser.builder()
                .name(notRegisteredUser.name())
                .build();

        when(userService.create(notRegisteredUser)).thenReturn(user);

        NotRegisteredUser result = (NotRegisteredUser) userController.create(notRegisteredUser);

        assertEquals(user, result);
        verify(userService, times(1)).create(notRegisteredUser);
    }

    @Test
    void getAll() {
        User user1 = User.builder()
                .id(1L)
                .name("user 1")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("user 2")
                .build();

        List<User> expectedUsers = List.of(user1, user2);

        when(userService.getAll()).thenReturn(expectedUsers);

        List<User> result = userController.getAll();

        assertEquals(expectedUsers, result);
        verify(userService, times(1)).getAll();
    }
}
