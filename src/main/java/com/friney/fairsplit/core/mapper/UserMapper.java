package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.entity.user.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    default UserDto mapUser(User user) {
        String displayName;
        if (user instanceof RegisteredUser registeredUser) {
            displayName = registeredUser.getName() + " (" + registeredUser.getEmail() + ")";
        } else {
            displayName = user.getName();
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .displayName(displayName)
                .build();
    }

    default RegisteredUserDto mapRegisteredUser(RegisteredUser registeredUser) {
        return RegisteredUserDto.builder()
                .id(registeredUser.getId())
                .name(registeredUser.getName())
                .displayName(registeredUser.getName() + " (" + registeredUser.getEmail() + ")")
                .email(registeredUser.getEmail())
                .build();
    }

    default List<UserDto> mapUser(List<User> users) {
        return users.stream()
                .map(this::mapUser)
                .toList();
    }
}
