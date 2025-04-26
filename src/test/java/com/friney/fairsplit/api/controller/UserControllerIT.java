package com.friney.fairsplit.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
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
    @Transactional
    void testCreateAndGetNotRegisteredUser() throws Exception {
        CreateNotRegisteredUserDto userDto = CreateNotRegisteredUserDto.builder()
                .name("user")
                .build();

        mockMvc.perform(post(Paths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpectAll(
                        status().isCreated(),
                        content().json(new ObjectMapper().writeValueAsString(userDto))
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
    @Transactional
    void testCreateAndGetRegisteredUser() throws Exception {
        CreateRegisteredUserDto userDto = CreateRegisteredUserDto.builder()
                .name("user")
                .email("email@email.com")
                .build();

        mockMvc.perform(post(Paths.USERS + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpectAll(
                        status().isCreated(),
                        content().json(new ObjectMapper().writeValueAsString(userDto))
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
    @Transactional
    void testCreateTwoRegisteredUsersWithSameEmail() throws Exception {
        CreateRegisteredUserDto userDto = CreateRegisteredUserDto.builder()
                .name("user")
                .email("email@email.com")
                .build();

        mockMvc.perform(post(Paths.USERS + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpectAll(
                        status().isCreated(),
                        content().json(new ObjectMapper().writeValueAsString(userDto))
                );

        mockMvc.perform(post(Paths.USERS + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    @Transactional
    void testCreateTwoNotRegisteredUsersWithSameName() throws Exception {
        CreateNotRegisteredUserDto userDto = CreateNotRegisteredUserDto.builder()
                .name("user")
                .build();

        mockMvc.perform(post(Paths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpectAll(
                        status().isCreated(),
                        content().json(new ObjectMapper().writeValueAsString(userDto))
                );

        mockMvc.perform(post(Paths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    @Transactional
    void testGetAllUsersEmpty() throws Exception {
        mockMvc.perform(get(Paths.USERS))
                .andExpectAll(
                        status().isOk(),
                        content().json(new ObjectMapper().writeValueAsString(List.of())
                        )
                );
    }

    @Test
    @Transactional
    void testGetAllUsers() throws Exception {
        CreateNotRegisteredUserDto userDto = CreateNotRegisteredUserDto.builder()
                .name("user")
                .build();
        mockMvc.perform(post(Paths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpectAll(
                        status().isCreated(),
                        content().json(new ObjectMapper().writeValueAsString(userDto))
                );

        CreateRegisteredUserDto userDto2 = CreateRegisteredUserDto.builder()
                .name("User2")
                .email("email@email.com")
                .build();
        mockMvc.perform(post(Paths.USERS + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto2)))
                .andExpectAll(
                        status().isCreated(),
                        content().json(new ObjectMapper().writeValueAsString(userDto2))
                );

        mockMvc.perform(get(Paths.USERS))
                .andExpectAll(
                        status().isOk(),
                        content().json(new ObjectMapper().writeValueAsString(List.of(userDto, userDto2)))
                );
    }
}
