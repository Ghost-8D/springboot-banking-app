package com.banking.securetransactionapi.service;

import com.banking.securetransactionapi.entity.Account;
import com.banking.securetransactionapi.entity.Role;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
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
        testAccount.setBalance(new BigDecimal("1000.00"));
    }

    @Test
    void findByUserId_WithValidUserId_ShouldReturnAccount() {
        // Arrange
        when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(testAccount));

        // Act
        Account result = accountService.findByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());

        verify(accountRepository).findByUserId(1L);
    }

    @Test
    void findByUserId_WithInvalidUserId_ShouldThrowException() {
        // Arrange
        when(accountRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.findByUserId(999L);
        });

        assertEquals("Account not found for user", exception.getMessage());
        verify(accountRepository).findByUserId(999L);
    }

    @Test
    void hasSufficientBalance_WithSufficientFunds_ShouldReturnTrue() {
        // Arrange
        BigDecimal amount = new BigDecimal("500.00");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        boolean result = accountService.hasSufficientBalance(1L, amount);

        // Assert
        assertTrue(result);
        verify(accountRepository).findById(1L);
    }

    @Test
    void hasSufficientBalance_WithInsufficientFunds_ShouldReturnFalse() {
        // Arrange
        BigDecimal amount = new BigDecimal("2000.00");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        boolean result = accountService.hasSufficientBalance(1L, amount);

        // Assert
        assertFalse(result);
        verify(accountRepository).findById(1L);
    }
}
