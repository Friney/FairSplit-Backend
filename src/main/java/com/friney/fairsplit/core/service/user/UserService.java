package com.friney.fairsplit.core.service.user;

import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.user.RegisteredUser;
import com.friney.fairsplit.core.entity.user.User;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    List<UserDto> getAll();

    User getById(Long id);

    UserDto addRegisteredUser(RegisteredUser user);

    UserDto addNotRegisteredUser(CreateNotRegisteredUserDto user);

    RegisteredUser findByEmail(String email);

    @Override
    UserDetails loadUserByUsername(String username);
}
