package com.banking.securetransactionapi.controller;

import com.banking.securetransactionapi.dto.TransactionRequest;
import com.banking.securetransactionapi.dto.TransactionResponse;
import com.banking.securetransactionapi.dto.TransferRequest;
import com.banking.securetransactionapi.entity.Role;
import com.banking.securetransactionapi.entity.TransactionType;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.banking.securetransactionapi.TestConfig;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;

@WebMvcTest(TransactionController.class)
@Import(TestConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(Role.ROLE_USER);

        transactionResponse = new TransactionResponse();
        transactionResponse.setId(1L);
        transactionResponse.setType(TransactionType.DEPOSIT);
        transactionResponse.setAmount(new BigDecimal("100.00"));
        transactionResponse.setDescription("Test transaction");
        transactionResponse.setTimestamp(LocalDateTime.now());
        transactionResponse.setBalanceAfter(new BigDecimal("1100.00"));
    }
    
    private Authentication createAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Test
    void deposit_WithValidRequest_ShouldReturnTransactionResponse() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Test deposit");

        when(transactionService.deposit(any(User.class), eq(new BigDecimal("100.00")), eq("Test deposit")))
                .thenReturn(transactionResponse);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/deposit")
                        .with(authentication(createAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.balanceAfter").value(1100.00));
    }

    @Test
    void deposit_WithInvalidAmount_ShouldReturnBadRequest() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("-100.00")); // Invalid negative amount
        request.setDescription("Test deposit");

        // Act & Assert
        mockMvc.perform(post("/api/transactions/deposit")
                        .with(authentication(createAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_WithValidRequest_ShouldReturnTransactionResponse() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("50.00"));
        request.setDescription("Test withdrawal");

        transactionResponse.setType(TransactionType.WITHDRAWAL);
        transactionResponse.setAmount(new BigDecimal("50.00"));
        transactionResponse.setBalanceAfter(new BigDecimal("950.00"));

        when(transactionService.withdraw(any(User.class), eq(new BigDecimal("50.00")), eq("Test withdrawal")))
                .thenReturn(transactionResponse);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/withdraw")
                        .with(authentication(createAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void withdraw_WithInsufficientBalance_ShouldReturnBadRequest() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("2000.00"));
        request.setDescription("Test withdrawal");

        when(transactionService.withdraw(any(User.class), eq(new BigDecimal("2000.00")), eq("Test withdrawal")))
                .thenThrow(new RuntimeException("Insufficient balance"));

        // Act & Assert
        mockMvc.perform(post("/api/transactions/withdraw")
                        .with(authentication(createAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transfer_WithValidRequest_ShouldReturnTransactionResponse() throws Exception {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setAmount(new BigDecimal("75.00"));
        request.setTargetAccountId(2L);
        request.setDescription("Test transfer");

        transactionResponse.setType(TransactionType.TRANSFER_OUT);
        transactionResponse.setAmount(new BigDecimal("75.00"));
        transactionResponse.setTargetAccountId(2L);
        transactionResponse.setBalanceAfter(new BigDecimal("925.00"));

        when(transactionService.transfer(any(User.class), eq(2L), eq(new BigDecimal("75.00")), eq("Test transfer")))
                .thenReturn(transactionResponse);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/transfer")
                        .with(authentication(createAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TRANSFER_OUT"))
                .andExpect(jsonPath("$.targetAccountId").value(2));
    }

    @Test
    void transfer_WithInvalidTargetAccount_ShouldReturnBadRequest() throws Exception {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setAmount(new BigDecimal("75.00"));
        request.setTargetAccountId(999L); // Non-existent account
        request.setDescription("Test transfer");

        when(transactionService.transfer(any(User.class), eq(999L), eq(new BigDecimal("75.00")), eq("Test transfer")))
                .thenThrow(new RuntimeException("Account not found"));

        // Act & Assert
        mockMvc.perform(post("/api/transactions/transfer")
                        .with(authentication(createAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransactionHistory_ShouldReturnTransactionList() throws Exception {
        // Arrange
        TransactionResponse transaction1 = new TransactionResponse();
        transaction1.setId(1L);
        transaction1.setType(TransactionType.DEPOSIT);
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setTimestamp(LocalDateTime.now());

        TransactionResponse transaction2 = new TransactionResponse();
        transaction2.setId(2L);
        transaction2.setType(TransactionType.WITHDRAWAL);
        transaction2.setAmount(new BigDecimal("50.00"));
        transaction2.setTimestamp(LocalDateTime.now());

        List<TransactionResponse> history = Arrays.asList(transaction1, transaction2);
        when(transactionService.getTransactionHistory(any(User.class)))
                .thenReturn(history);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/history")
                        .with(authentication(createAuthentication(testUser))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"));
    }

    @Test
    void deposit_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Test deposit");

        // Act & Assert
        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTransactionHistory_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/transactions/history"))
                .andExpect(status().isUnauthorized());
    }
}
