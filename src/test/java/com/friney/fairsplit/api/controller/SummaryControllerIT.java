package com.friney.fairsplit.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friney.fairsplit.api.Paths;
import com.friney.fairsplit.api.dto.event.EventCreateRequest;
import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.expense.ExpenseCreateRequest;
import com.friney.fairsplit.api.dto.expense.ExpenseDto;
import com.friney.fairsplit.api.dto.expense.member.ExpenseMemberCreateRequest;
import com.friney.fairsplit.api.dto.receipt.ReceiptCreateRequest;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.api.dto.summary.ExpenseSummaryDto;
import com.friney.fairsplit.api.dto.summary.ReceiptSummaryDto;
import com.friney.fairsplit.api.dto.summary.SummaryDto;
import com.friney.fairsplit.api.dto.user.CreateNotRegisteredUserRequest;
import com.friney.fairsplit.api.dto.user.CreateRegisteredUserRequest;
import com.friney.fairsplit.api.dto.user.UserDto;
import com.friney.fairsplit.core.entity.summary.Debt;
import com.friney.fairsplit.core.entity.summary.PayerInfo;
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
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

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
        createNotRegisteredUser("controller 1");
        createNotRegisteredUser("controller 2");
        createRegisteredUser("controller 3", userEmailOwnerEvent);
        createRegisteredUser("controller 4", "email2@email.com");

        EventDto eventDto = createEvent();

        saveInTransactional();
        SummaryDto expected = SummaryDto.builder()
                .total(BigDecimal.ZERO)
                .debts(List.of())
                .payerInfos(List.of())
                .receipts(List.of())
                .build();
        testSummaryEndpoint(eventDto.id(), expected);
    }

    @Test
    @WithMockUser(username = userEmailOwnerEvent)
    void testCorrectCollectionResultWithoutExpenses() throws Exception {
        User user1 = createNotRegisteredUser("controller 1");
        User user2 = createNotRegisteredUser("controller 2");
        createRegisteredUser("controller 3", userEmailOwnerEvent);
        createRegisteredUser("controller 4", "email2@email.com");

        EventDto eventDto = createEvent();

        createReceipt(eventDto.id(), "receipt 1", user1.getId());
        createReceipt(eventDto.id(), "receipt 2", user2.getId());

        saveInTransactional();

        SummaryDto expected = SummaryDto.builder()
                .total(BigDecimal.ZERO)
                .debts(List.of())
                .payerInfos(List.of())
                .receipts(List.of(
                        ReceiptSummaryDto.builder().name("receipt 1").total(BigDecimal.valueOf(0)).payerInfos(List.of()).expenses(List.of()).build(),
                        ReceiptSummaryDto.builder().name("receipt 2").total(BigDecimal.valueOf(0)).payerInfos(List.of()).expenses(List.of()).build()
                ))
                .build();
        testSummaryEndpoint(eventDto.id(), expected);
    }

    @Test
    @WithMockUser(username = userEmailOwnerEvent)
    void testCorrectCollectionResultWithoutExpensesMember() throws Exception {
        User user1 = createNotRegisteredUser("controller 1");
        User user2 = createNotRegisteredUser("controller 2");
        createRegisteredUser("controller 3", userEmailOwnerEvent);
        createRegisteredUser("controller 4", "email2@email.com");

        EventDto eventDto = createEvent();

        ReceiptDto receipt1 = createReceipt(eventDto.id(), "receipt 1", user1.getId());
        ReceiptDto receipt2 = createReceipt(eventDto.id(), "receipt 2", user2.getId());

        createExpense(receipt1.id(), "expense 1", BigDecimal.valueOf(100));
        createExpense(receipt1.id(), "expense 2", BigDecimal.valueOf(100));
        createExpense(receipt2.id(), "expense 3", BigDecimal.valueOf(300));

        saveInTransactional();

        ReceiptSummaryDto rs1 = ReceiptSummaryDto.builder()
                .name(receipt1.name())
                .total(BigDecimal.valueOf(200.0))
                .payerInfos(List.of())
                .expenses(List.of(
                        ExpenseSummaryDto.builder().name("expense 1").total(BigDecimal.valueOf(100)).payerInfos(List.of()).build(),
                        ExpenseSummaryDto.builder().name("expense 2").total(BigDecimal.valueOf(100)).payerInfos(List.of()).build()
                ))
                .build();
        ReceiptSummaryDto rs2 = ReceiptSummaryDto.builder()
                .name(receipt2.name())
                .total(BigDecimal.valueOf(300.0))
                .payerInfos(List.of())
                .expenses(List.of(
                        ExpenseSummaryDto.builder().name("expense 3").total(BigDecimal.valueOf(300)).payerInfos(List.of()).build()
                ))
                .build();
        SummaryDto expected = SummaryDto.builder()
                .total(BigDecimal.valueOf(500))
                .debts(List.of())
                .payerInfos(List.of())
                .receipts(List.of(rs1, rs2))
                .build();
        testSummaryEndpoint(eventDto.id(), expected);
    }

    @Test
    @WithMockUser(username = userEmailOwnerEvent)
    void testCorrectCollectionResult() throws Exception {
        User user1 = createNotRegisteredUser("controller 1");
        User user2 = createNotRegisteredUser("controller 2");
        User user3 = createRegisteredUser("controller 3", userEmailOwnerEvent);
        User user4 = createRegisteredUser("controller 4", "email2@email.com");

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

        UserDto userDto1 = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .displayName(user1.getName())
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(user2.getId())
                .name(user2.getName())
                .displayName(user2.getName())
                .build();
        UserDto userDto3 = UserDto.builder()
                .id(user3.getId())
                .name(user3.getName())
                .displayName(user3.getName() + " (" + userEmailOwnerEvent + ")")
                .build();
        UserDto userDto4 = UserDto.builder()
                .id(user4.getId())
                .name(user4.getName())
                .displayName(user4.getName() + " (email2@email.com)")
                .build();

        PayerInfo payer1 = PayerInfo.builder()
                .user(userDto1)
                .total(new BigDecimal("108.33"))
                .build();
        PayerInfo payer2 = PayerInfo.builder()
                .user(userDto2)
                .total(new BigDecimal("158.33"))
                .build();
        PayerInfo payer3 = PayerInfo.builder()
                .user(userDto3)
                .total(new BigDecimal("125.00"))
                .build();
        PayerInfo payer4 = PayerInfo.builder()
                .user(userDto4)
                .total(new BigDecimal("108.33"))
                .build();

        ExpenseSummaryDto expenseSummaryDto1 = ExpenseSummaryDto.builder()
                .name("expense 1")
                .total(new BigDecimal("100.00"))
                .payerInfos(List.of(
                        PayerInfo.builder().user(userDto1).total(new BigDecimal("33.33")).build(),
                        PayerInfo.builder().user(userDto2).total(new BigDecimal("33.33")).build(),
                        PayerInfo.builder().user(userDto4).total(new BigDecimal("33.33")).build()
                ))
                .build();

        ExpenseSummaryDto expenseSummaryDto2 = ExpenseSummaryDto.builder()
                .name("expense 2")
                .total(new BigDecimal("100.00"))
                .payerInfos(List.of(
                        PayerInfo.builder().user(userDto2).total(new BigDecimal("50.00")).build(),
                        PayerInfo.builder().user(userDto3).total(new BigDecimal("50.00")).build()
                ))
                .build();

        ExpenseSummaryDto expenseSummaryDto3 = ExpenseSummaryDto.builder()
                .name("expense 3")
                .total(new BigDecimal("300.00"))
                .payerInfos(List.of(
                        PayerInfo.builder().user(userDto1).total(new BigDecimal("75.00")).build(),
                        PayerInfo.builder().user(userDto2).total(new BigDecimal("75.00")).build(),
                        PayerInfo.builder().user(userDto3).total(new BigDecimal("75.00")).build(),
                        PayerInfo.builder().user(userDto4).total(new BigDecimal("75.00")).build()
                ))
                .build();

        ReceiptSummaryDto rs1 = ReceiptSummaryDto.builder()
                .name(receipt1.name())
                .total(BigDecimal.valueOf(200.0))
                .payerInfos(List.of(
                        PayerInfo.builder()
                                .user(userDto1)
                                .total(new BigDecimal("33.33"))
                                .build(),
                        PayerInfo.builder()
                                .user(userDto2)
                                .total(new BigDecimal("83.33"))
                                .build(),
                        PayerInfo.builder()
                                .user(userDto3)
                                .total(new BigDecimal("50.00"))
                                .build(),
                        PayerInfo.builder()
                                .user(userDto4)
                                .total(new BigDecimal("33.33"))
                                .build()
                ))
                .expenses(List.of(expenseSummaryDto1, expenseSummaryDto2))
                .build();
        ReceiptSummaryDto rs2 = ReceiptSummaryDto.builder()
                .name(receipt2.name())
                .total(BigDecimal.valueOf(300.0))
                .payerInfos(List.of(
                        PayerInfo.builder()
                                .user(userDto1)
                                .total(new BigDecimal("75.00"))
                                .build(),
                        PayerInfo.builder()
                                .user(userDto2)
                                .total(new BigDecimal("75.00"))
                                .build(),
                        PayerInfo.builder()
                                .user(userDto3)
                                .total(new BigDecimal("75.00"))
                                .build(),
                        PayerInfo.builder()
                                .user(userDto4)
                                .total(new BigDecimal("75.00"))
                                .build()
                ))
                .expenses(List.of(expenseSummaryDto3))
                .build();
        Debt d1 = Debt.builder()
                .from(userDto4)
                .to(userDto1)
                .amount(BigDecimal.valueOf(33.33))
                .build();
        Debt d2 = Debt.builder()
                .from(userDto2)
                .to(userDto1)
                .amount(BigDecimal.valueOf(83.33))
                .build();
        Debt d3 = Debt.builder()
                .from(userDto3)
                .to(userDto1)
                .amount(BigDecimal.valueOf(50.00))
                .build();
        Debt d4 = Debt.builder()
                .from(userDto4)
                .to(userDto2)
                .amount(BigDecimal.valueOf(75.00))
                .build();
        Debt d5 = Debt.builder()
                .from(userDto1)
                .to(userDto2)
                .amount(BigDecimal.valueOf(75.00))
                .build();
        Debt d6 = Debt.builder()
                .from(userDto3)
                .to(userDto2)
                .amount(BigDecimal.valueOf(75.00))
                .build();

        SummaryDto expected = SummaryDto.builder()
                .total(BigDecimal.valueOf(500))
                .debts(List.of(d1, d2, d3, d4, d5, d6))
                .payerInfos(List.of(payer1, payer2, payer3, payer4))
                .receipts(List.of(rs1, rs2))
                .build();
        testSummaryEndpoint(eventDto.id(), expected);
    }

    private EventDto createEvent() throws Exception {
        EventCreateRequest createDto = EventCreateRequest.builder()
                .name("event")
                .description("description")
                .build();

        String response = mockMvc.perform(post(Paths.EVENTS_V1)
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
        CreateNotRegisteredUserRequest userDto = CreateNotRegisteredUserRequest.builder()
                .name(name)
                .build();

        String response = mockMvc.perform(post(Paths.USERS_V1)
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
        CreateRegisteredUserRequest userDto = CreateRegisteredUserRequest.builder()
                .name(name)
                .email(email)
                .password("password")
                .confirmPassword("password")
                .build();

        String response = mockMvc.perform(post(Paths.AUTH_V1 + "/registration")
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
        ReceiptCreateRequest receiptDto = ReceiptCreateRequest.builder()
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
                .paidByUser(objectMapper.treeToValue(objectMapper.readTree(response).get("paidByUser"), UserDto.class))
                .build();
    }

    private ExpenseDto createExpense(Long receiptId, String name, BigDecimal amount) throws Exception {
        ExpenseCreateRequest expenseDto = ExpenseCreateRequest.builder()
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
        ExpenseMemberCreateRequest expenseMemberDto = ExpenseMemberCreateRequest.builder()
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

    private void testSummaryEndpoint(Long id, SummaryDto expected) throws Exception {
        mockMvc.perform(get("/events/" + id + "/summary"))
                .andExpectAll(
                        status().isOk(),
                        content().json(new ObjectMapper().writeValueAsString(expected))
                );
    }

}
