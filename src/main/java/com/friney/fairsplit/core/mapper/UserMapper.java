package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.entity.user.User;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    default UserDto map(User user) {
        String displayName;
        if (user instanceof RegisteredUser registeredUser) {
            displayName = registeredUser.getName() + " (" + registeredUser.getEmail() + ")";
        } else {
            displayName = user.getName();
        }
        return UserDto.builder()
                .id(user.getId())
                .displayName(displayName)
                .build();
    }

    default List<UserDto> map(List<User> users) {
        return users.stream()
                .map(this::map)
                .toList();
    }
}
