package com.friney.fairsplit.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @WithMockUser
    void testCreateAndGetNotRegisteredUser() throws Exception {
        CreateNotRegisteredUserDto userDto = CreateNotRegisteredUserDto.builder()
                .name("user")
                .build();

        mockMvc.perform(post(Paths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name").value(userDto.name())
                )
                .andDo(
                        result -> {
                            String response = result.getResponse().getContentAsString();
                            long id = new ObjectMapper().readTree(response).get("id").asLong();
                            assertTrue(userRepository.existsById(id));
                        }
                );

    }

    @Test
    @WithMockUser
    void testCreateAndGetRegisteredUser() throws Exception {
        CreateRegisteredUserDto createRegisteredUserDto = CreateRegisteredUserDto.builder()
                .name("user")
                .email("email@email.com")
                .password("password")
                .confirmPassword("password")
                .build();
        UserDto registeredUserDto = UserDto.builder()
                .name(createRegisteredUserDto.name())
                .displayName(createRegisteredUserDto.name() + " (" + createRegisteredUserDto.email() + ")")
                .build();

        mockMvc.perform(post(Paths.AUTH + "/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createRegisteredUserDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name").value(registeredUserDto.name()),
                        jsonPath("$.displayName").value(registeredUserDto.displayName())
                )
                .andDo(
                        result -> {
                            String response = result.getResponse().getContentAsString();
                            long id = new ObjectMapper().readTree(response).get("id").asLong();
                            assertTrue(userRepository.existsById(id));
                        }
                );
    }

    @Test
    @WithMockUser
    void testCreateTwoNotRegisteredUsersWithSameName() throws Exception {
        CreateNotRegisteredUserDto createNotRegisteredUserDto = CreateNotRegisteredUserDto.builder()
                .name("user")
                .build();
        UserDto notRegistereduserDto = UserDto.builder()
                .name(createNotRegisteredUserDto.name())
                .displayName(createNotRegisteredUserDto.name())
                .build();

        mockMvc.perform(post(Paths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createNotRegisteredUserDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name").value(notRegistereduserDto.name()),
                        jsonPath("$.displayName").value(notRegistereduserDto.displayName())
                );

        mockMvc.perform(post(Paths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createNotRegisteredUserDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name").value(notRegistereduserDto.name()),
                        jsonPath("$.displayName").value(notRegistereduserDto.displayName())
                );
    }

    @Test
    @WithMockUser
    void testGetAllUsersEmpty() throws Exception {
        mockMvc.perform(get(Paths.USERS))
                .andExpectAll(
                        status().isOk(),
                        content().json(new ObjectMapper().writeValueAsString(List.of())
                        )
                );
    }

    @Test
    @WithMockUser
    void testGetAllUsers() throws Exception {
        CreateNotRegisteredUserDto createNotRegisteredUserDto = CreateNotRegisteredUserDto.builder()
                .name("user")
                .build();
        UserDto notRegistereduserDto = UserDto.builder()
                .name(createNotRegisteredUserDto.name())
                .displayName(createNotRegisteredUserDto.name())
                .build();
        mockMvc.perform(post(Paths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createNotRegisteredUserDto))
                )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name").value(notRegistereduserDto.name()),
                        jsonPath("$.displayName").value(notRegistereduserDto.displayName())
                );

        CreateRegisteredUserDto createRegisteredUserDto = CreateRegisteredUserDto.builder()
                .name("User2")
                .email("email@email.com")
                .password("password")
                .confirmPassword("password")
                .build();
        UserDto registeredUserDto = UserDto.builder()
                .name(createRegisteredUserDto.name())
                .displayName(createRegisteredUserDto.name() + " (" + createRegisteredUserDto.email() + ")")
                .build();

        mockMvc.perform(post(Paths.AUTH + "/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createRegisteredUserDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name").value(registeredUserDto.name()),
                        jsonPath("$.displayName").value(registeredUserDto.displayName())
                );

        mockMvc.perform(get(Paths.USERS))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].name").value(notRegistereduserDto.name()),
                        jsonPath("$[0].displayName").value(notRegistereduserDto.displayName()),
                        jsonPath("$[1].name").value(registeredUserDto.name()),
                        jsonPath("$[1].displayName").value(registeredUserDto.displayName())
                );
    }
}
