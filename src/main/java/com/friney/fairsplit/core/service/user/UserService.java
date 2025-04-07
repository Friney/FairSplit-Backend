package com.friney.fairsplit.core.service.user;

import com.friney.fairsplit.api.dto.User.NotRegisteredUserDto;
import com.friney.fairsplit.api.dto.User.RegisteredUserDto;
import com.friney.fairsplit.core.entity.User.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(Long id);

    User create(RegisteredUserDto user);

    User create(NotRegisteredUserDto user);
}
