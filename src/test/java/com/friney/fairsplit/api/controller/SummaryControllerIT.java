package com.friney.fairsplit.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.event.EventCreateDto;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.expense.ExpenseCreateDto;
import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberCreateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptCreateDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserDto;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserDto;
import com.friney.fairsplit.core.entity.summary.Debt;
import com.friney.fairsplit.core.entity.summary.ReceiptSummary;
import com.friney.fairsplit.core.entity.summary.Summary;
import com.friney.fairsplit.core.entity.user.User;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class SummaryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String userEmailOwnerEvent = "email1@email.com";

    @Test
    @WithMockUser(username = userEmailOwnerEvent)
    void testCorrectCollectionResultWithoutReceipts() throws Exception {
        createNotRegisteredUser("user 1");
        createNotRegisteredUser("user 2");
        createRegisteredUser("user 3", userEmailOwnerEvent);
        createRegisteredUser("user 4", "email2@email.com");

        EventDto eventDto = createEvent();

        saveInTransactional();
        Summary expected = Summary.builder()
                .total(BigDecimal.ZERO)
                .receipts(List.of())
                .build();
        testSummaryEndpoint(eventDto.id(), expected);
    }

    @Test
    @WithMockUser(username = userEmailOwnerEvent)
    void testCorrectCollectionResultWithoutExpenses() throws Exception {
        User user1 = createNotRegisteredUser("user 1");
        User user2 = createNotRegisteredUser("user 2");
        createRegisteredUser("user 3", userEmailOwnerEvent);
        createRegisteredUser("user 4", "email2@email.com");

        EventDto eventDto = createEvent();

        createReceipt(eventDto.id(), "receipt 1", user1.getId());
        createReceipt(eventDto.id(), "receipt 2", user2.getId());

        saveInTransactional();

        Summary expected = Summary.builder()
                .total(BigDecimal.ZERO)
                .receipts(List.of())
                .build();
        testSummaryEndpoint(eventDto.id(), expected);
    }

    @Test
    @WithMockUser(username = userEmailOwnerEvent)
    void testCorrectCollectionResultWithoutExpensesMember() throws Exception {
        User user1 = createNotRegisteredUser("user 1");
        User user2 = createNotRegisteredUser("user 2");
        createRegisteredUser("user 3", userEmailOwnerEvent);
        createRegisteredUser("user 4", "email2@email.com");

        EventDto eventDto = createEvent();

        ReceiptDto receipt1 = createReceipt(eventDto.id(), "receipt 1", user1.getId());
        ReceiptDto receipt2 = createReceipt(eventDto.id(), "receipt 2", user2.getId());

        createExpense(receipt1.id(), "expense 1", BigDecimal.valueOf(100));
        createExpense(receipt1.id(), "expense 2", BigDecimal.valueOf(100));
        createExpense(receipt2.id(), "expense 3", BigDecimal.valueOf(300));

        saveInTransactional();

        Summary expected = Summary.builder()
                .total(BigDecimal.valueOf(500))
                .receipts(List.of(
                        new ReceiptSummary(receipt1.name(), BigDecimal.valueOf(200.0), List.of()),
                        new ReceiptSummary(receipt2.name(), BigDecimal.valueOf(300.0), List.of())
                ))
                .build();
        testSummaryEndpoint(eventDto.id(), expected);
    }

    @Test
    @WithMockUser(username = userEmailOwnerEvent)
    void testCorrectCollectionResult() throws Exception {
        User user1 = createNotRegisteredUser("user 1");
        User user2 = createNotRegisteredUser("user 2");
        User user3 = createRegisteredUser("user 3", userEmailOwnerEvent);
        User user4 = createRegisteredUser("user 4", "email2@email.com");

        EventDto eventDto = createEvent();

        ReceiptDto receipt1 = createReceipt(eventDto.id(), "receipt 1", user1.getId());
        ReceiptDto receipt2 = createReceipt(eventDto.id(), "receipt 2", user2.getId());

        ExpenseDto expense1 = createExpense(receipt1.id(), "expense 1", BigDecimal.valueOf(100));
        ExpenseDto expense2 = createExpense(receipt1.id(), "expense 2", BigDecimal.valueOf(100));
        ExpenseDto expense3 = createExpense(receipt2.id(), "expense 3", BigDecimal.valueOf(300));

        createExpenseMember(expense1.id(), user1.getId());
        createExpenseMember(expense1.id(), user2.getId());
        createExpenseMember(expense1.id(), user4.getId());

        createExpenseMember(expense2.id(), user2.getId());
        createExpenseMember(expense2.id(), user3.getId());

        createExpenseMember(expense3.id(), user1.getId());
        createExpenseMember(expense3.id(), user2.getId());
        createExpenseMember(expense3.id(), user3.getId());
        createExpenseMember(expense3.id(), user4.getId());

        saveInTransactional();

        Summary expected = Summary.builder()
                .total(BigDecimal.valueOf(500))
                .receipts(List.of(
                        new ReceiptSummary(
                                receipt1.name(),
                                BigDecimal.valueOf(200.0),
                                List.of(
                                        new Debt("user 2", "user 1", BigDecimal.valueOf(83.33)),
                                        new Debt("user 3", "user 1", BigDecimal.valueOf(50.00)),
                                        new Debt("user 4", "user 1", BigDecimal.valueOf(33.33))
                                )),
                        new ReceiptSummary(
                                receipt2.name(),
                                BigDecimal.valueOf(300.0),
                                List.of(
                                        new Debt("user 1", "user 2", BigDecimal.valueOf(75.00)),
                                        new Debt("user 3", "user 2", BigDecimal.valueOf(75.00)),
                                        new Debt("user 4", "user 2", BigDecimal.valueOf(75.00))
                                ))
                ))
                .build();
        testSummaryEndpoint(eventDto.id(), expected);
    }

    private EventDto createEvent() throws Exception {
        EventCreateDto createDto = EventCreateDto.builder()
                .name("event")
                .description("description")
                .build();

        String response = mockMvc.perform(post(Paths.EVENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return EventDto.builder()
                .name(objectMapper.readTree(response).get("name").asText())
                .description(objectMapper.readTree(response).get("description").asText())
                .id(objectMapper.readTree(response).get("id").asLong())
                .build();
    }

    private User createNotRegisteredUser(String name) throws Exception {
        CreateNotRegisteredUserDto userDto = CreateNotRegisteredUserDto.builder()
                .name(name)
                .build();

        String response = mockMvc.perform(post(Paths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return User.builder()
                .name(objectMapper.readTree(response).get("name").asText())
                .id(objectMapper.readTree(response).get("id").asLong())
                .build();
    }

    private User createRegisteredUser(String name, String email) throws Exception {
        CreateRegisteredUserDto userDto = CreateRegisteredUserDto.builder()
                .name(name)
                .email(email)
                .password("password")
                .confirmPassword("password")
                .build();

        String response = mockMvc.perform(post(Paths.AUTH + "/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return User.builder()
                .name(objectMapper.readTree(response).get("name").asText())
                .id(objectMapper.readTree(response).get("id").asLong())
                .build();
    }

    private ReceiptDto createReceipt(Long eventId, String name, Long userId) throws Exception {
        ReceiptCreateDto receiptDto = ReceiptCreateDto.builder()
                .name(name)
                .userId(userId)
                .build();

        String response = mockMvc.perform(post("/events/" + eventId + "/receipts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiptDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return ReceiptDto.builder()
                .id(objectMapper.readTree(response).get("id").asLong())
                .name(objectMapper.readTree(response).get("name").asText())
                .paidByUserName(objectMapper.readTree(response).get("paidByUserName").asText())
                .build();
    }

    private ExpenseDto createExpense(Long receiptId, String name, BigDecimal amount) throws Exception {
        ExpenseCreateDto expenseDto = ExpenseCreateDto.builder()
                .name(name)
                .amount(amount)
                .build();

        String response = mockMvc.perform(post("/receipts/" + receiptId + "/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return ExpenseDto.builder()
                .id(objectMapper.readTree(response).get("id").asLong())
                .name(objectMapper.readTree(response).get("name").asText())
                .amount(objectMapper.readTree(response).get("amount").decimalValue())
                .build();
    }

    private void createExpenseMember(Long eventId, Long userId) throws Exception {
        ExpenseMemberCreateDto expenseMemberDto = ExpenseMemberCreateDto.builder()
                .userId(userId)
                .build();

        mockMvc.perform(post("/expenses/" + eventId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseMemberDto)))
                .andExpect(status().isCreated());
    }

    private void saveInTransactional() {
        entityManager.flush();
        entityManager.clear();
    }

    private void testSummaryEndpoint(Long id, Summary expected) throws Exception {
        mockMvc.perform(get("/events/" + id + "/summary"))
                .andExpectAll(
                        status().isOk(),
                        content().json(new ObjectMapper().writeValueAsString(expected))
                );
    }

}