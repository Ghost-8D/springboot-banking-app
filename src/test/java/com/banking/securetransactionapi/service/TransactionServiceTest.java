package com.banking.securetransactionapi.service;

import com.banking.securetransactionapi.dto.TransactionResponse;
import com.banking.securetransactionapi.entity.*;
import com.banking.securetransactionapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Account testAccount;
    private Account targetAccount;

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

        targetAccount = new Account();
        targetAccount.setId(2L);
        targetAccount.setUserId(2L);
        targetAccount.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void deposit_ShouldCreateDepositTransaction() {
        // Arrange
        BigDecimal depositAmount = new BigDecimal("100.00");
        String description = "Test deposit";
        
        when(accountService.getUserAccount(testUser)).thenReturn(testAccount);
        when(accountService.updateBalance(anyLong(), any(BigDecimal.class)))
                .thenReturn(testAccount);
        
        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setAccountId(1L);
        savedTransaction.setType(TransactionType.DEPOSIT);
        savedTransaction.setAmount(depositAmount);
        savedTransaction.setDescription(description);
        savedTransaction.setTimestamp(LocalDateTime.now());
        
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        // Act
        TransactionResponse result = transactionService.deposit(testUser, depositAmount, description);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(depositAmount, result.getAmount());
        assertEquals(description, result.getDescription());
        assertEquals(new BigDecimal("1100.00"), result.getBalanceAfter());

        verify(accountService).getUserAccount(testUser);
        verify(accountService).updateBalance(1L, new BigDecimal("1100.00"));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_WithSufficientBalance_ShouldCreateWithdrawalTransaction() {
        // Arrange
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        String description = "Test withdrawal";
        
        when(accountService.getUserAccount(testUser)).thenReturn(testAccount);
        when(accountService.hasSufficientBalance(1L, withdrawAmount)).thenReturn(true);
        when(accountService.updateBalance(anyLong(), any(BigDecimal.class)))
                .thenReturn(testAccount);
        
        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setAccountId(1L);
        savedTransaction.setType(TransactionType.WITHDRAWAL);
        savedTransaction.setAmount(withdrawAmount);
        savedTransaction.setDescription(description);
        savedTransaction.setTimestamp(LocalDateTime.now());
        
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        // Act
        TransactionResponse result = transactionService.withdraw(testUser, withdrawAmount, description);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.WITHDRAWAL, result.getType());
        assertEquals(withdrawAmount, result.getAmount());
        assertEquals(description, result.getDescription());
        assertEquals(new BigDecimal("900.00"), result.getBalanceAfter());

        verify(accountService).hasSufficientBalance(1L, withdrawAmount);
        verify(accountService).updateBalance(1L, new BigDecimal("900.00"));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_WithInsufficientBalance_ShouldThrowException() {
        // Arrange
        BigDecimal withdrawAmount = new BigDecimal("2000.00");
        String description = "Test withdrawal";
        
        when(accountService.getUserAccount(testUser)).thenReturn(testAccount);
        when(accountService.hasSufficientBalance(1L, withdrawAmount)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.withdraw(testUser, withdrawAmount, description);
        });

        assertEquals("Insufficient balance", exception.getMessage());
        verify(accountService, never()).updateBalance(anyLong(), any(BigDecimal.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transfer_WithValidData_ShouldCreateTransferTransactions() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");
        String description = "Test transfer";
        Long targetAccountId = 2L;
        
        when(accountService.getUserAccount(testUser)).thenReturn(testAccount);
        when(accountService.findById(targetAccountId)).thenReturn(targetAccount);
        when(accountService.hasSufficientBalance(1L, transferAmount)).thenReturn(true);
        
        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setAccountId(1L);
        savedTransaction.setType(TransactionType.TRANSFER_OUT);
        savedTransaction.setAmount(transferAmount);
        savedTransaction.setTargetAccountId(targetAccountId);
        savedTransaction.setDescription(description);
        savedTransaction.setTimestamp(LocalDateTime.now());
        
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        // Act
        TransactionResponse result = transactionService.transfer(
                testUser, targetAccountId, transferAmount, description);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.TRANSFER_OUT, result.getType());
        assertEquals(transferAmount, result.getAmount());
        assertEquals(targetAccountId, result.getTargetAccountId());
        assertEquals(description, result.getDescription());
        assertEquals(new BigDecimal("900.00"), result.getBalanceAfter());

        verify(accountService).updateBalance(1L, new BigDecimal("900.00"));
        verify(accountService).updateBalance(2L, new BigDecimal("600.00"));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void getTransactionHistory_ShouldReturnTransactionList() {
        // Arrange
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setAccountId(1L);
        transaction1.setType(TransactionType.DEPOSIT);
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setTimestamp(LocalDateTime.now());

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAccountId(1L);
        transaction2.setType(TransactionType.WITHDRAWAL);
        transaction2.setAmount(new BigDecimal("50.00"));
        transaction2.setTimestamp(LocalDateTime.now());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        
        when(accountService.getUserAccount(testUser)).thenReturn(testAccount);
        when(transactionRepository.findByAccountIdOrderByTimestampDesc(1L))
                .thenReturn(transactions);

        // Act
        List<TransactionResponse> result = transactionService.getTransactionHistory(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TransactionType.DEPOSIT, result.get(0).getType());
        assertEquals(TransactionType.WITHDRAWAL, result.get(1).getType());

        verify(accountService).getUserAccount(testUser);
        verify(transactionRepository).findByAccountIdOrderByTimestampDesc(1L);
    }
}
