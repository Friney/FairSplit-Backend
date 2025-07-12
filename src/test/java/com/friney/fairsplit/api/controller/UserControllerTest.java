package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.api.dto.user.UserUpdateDto;
import com.friney.fairsplit.core.entity.user.NotRegisteredUser;
import com.friney.fairsplit.core.service.user.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testCreateNotRegisteredUser() {
        CreateNotRegisteredUserDto notRegisteredUser = CreateNotRegisteredUserDto.builder()
                .name("controller")
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
    void testGetAll() {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("controller 1")
                .displayName("controller 1")
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("controller 2")
                .displayName("controller 2 (example@example.com)")
                .build();

        List<UserDto> expectedUsers = List.of(user1, user2);

        when(userService.getAll()).thenReturn(expectedUsers);

        List<UserDto> result = userController.getAll();

        assertEquals(expectedUsers, result);
        verify(userService, times(1)).getAll();
    }

    @Test
    void testUpdate() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("new name")
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name("new name")
                .build();

        when(userService.updateNotRegisteredUser(userUpdateDto, 1L)).thenReturn(expectedUser);

        UserDto result = userController.update(userUpdateDto, 1L);

        assertEquals(expectedUser, result);
        verify(userService, times(1)).updateNotRegisteredUser(userUpdateDto, 1L);
    }
}
