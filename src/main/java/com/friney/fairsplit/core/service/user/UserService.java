package com.friney.fairsplit.core.service.user;

import com.friney.fairsplit.api.dto.user.NotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.RegisteredUserDto;
import com.friney.fairsplit.core.entity.user.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(Long id);

    User create(RegisteredUserDto user);

    User create(NotRegisteredUserDto user);
}
