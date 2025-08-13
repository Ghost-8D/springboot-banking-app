package com.banking.securetransactionapi.controller;

import com.banking.securetransactionapi.entity.Account;
import com.banking.securetransactionapi.entity.Role;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.exception.AccountNotFoundException;
import com.banking.securetransactionapi.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.banking.securetransactionapi.TestConfig;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;

@WebMvcTest(AccountController.class)
@Import(TestConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(Role.ROLE_USER);

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUserId(1L);
        testAccount.setBalance(new BigDecimal("1500.75"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getBalance_WithAuthenticatedUser_ShouldReturnAccountBalance() throws Exception {
        // Arrange
        when(accountService.getUserAccount(any(User.class))).thenReturn(testAccount);

        // Act & Assert
        mockMvc.perform(get("/api/account/balance")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.balance").value(1500.75))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(accountService).getUserAccount(any(User.class));
    }

    @Test
    void getBalance_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/account/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getBalance_WhenAccountNotFound_ShouldReturnError() throws Exception {
        // Arrange
        when(accountService.getUserAccount(any(User.class)))
                .thenThrow(new AccountNotFoundException("Account not found for user"));

        // Act & Assert
        mockMvc.perform(get("/api/account/balance")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error: Account not found for user"));

        verify(accountService).getUserAccount(any(User.class));
    }
}
