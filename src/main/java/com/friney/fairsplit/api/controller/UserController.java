package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.User.NotRegisteredUserDto;
import com.friney.fairsplit.api.dto.User.RegisteredUserDto;
import com.friney.fairsplit.core.entity.User.User;
import com.friney.fairsplit.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Paths.USERS)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public User create(@RequestBody RegisteredUserDto user) {
        return userService.create(user);
    }

    @PostMapping
    public User create(@RequestBody NotRegisteredUserDto user) {
        return userService.create(user);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }
}
