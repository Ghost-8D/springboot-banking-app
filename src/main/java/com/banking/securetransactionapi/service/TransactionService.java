package com.banking.securetransactionapi.service;

import com.banking.securetransactionapi.dto.TransactionResponse;
import com.banking.securetransactionapi.entity.Account;
import com.banking.securetransactionapi.entity.Transaction;
import com.banking.securetransactionapi.entity.TransactionType;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.exception.InsufficientFundsException;
import com.banking.securetransactionapi.exception.InvalidTransferException;
import com.banking.securetransactionapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    
    @Transactional
    public TransactionResponse deposit(User user, BigDecimal amount, String description) {
        Account account = accountService.getUserAccount(user);
        
        // Update balance
        BigDecimal newBalance = account.getBalance().add(amount);
        accountService.updateBalance(account.getId(), newBalance);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return mapToResponse(savedTransaction, newBalance);
    }
    
    @Transactional
    public TransactionResponse withdraw(User user, BigDecimal amount, String description) {
        Account account = accountService.getUserAccount(user);
        
        // Check sufficient balance
        if (!accountService.hasSufficientBalance(account.getId(), amount)) {
            throw new InsufficientFundsException("Insufficient balance for withdrawal");
        }
        
        // Update balance
        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountService.updateBalance(account.getId(), newBalance);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getId());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return mapToResponse(savedTransaction, newBalance);
    }
    
    @Transactional
    public TransactionResponse transfer(User user, Long targetAccountId, BigDecimal amount, String description) {
        Account sourceAccount = accountService.getUserAccount(user);
        Account targetAccount = accountService.findById(targetAccountId);
        
        // Validation
        if (sourceAccount.getId().equals(targetAccountId)) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }
        
        if (!accountService.hasSufficientBalance(sourceAccount.getId(), amount)) {
            throw new InsufficientFundsException("Insufficient balance for transfer");
        }
        
        // Update balances
        BigDecimal sourceNewBalance = sourceAccount.getBalance().subtract(amount);
        BigDecimal targetNewBalance = targetAccount.getBalance().add(amount);
        
        accountService.updateBalance(sourceAccount.getId(), sourceNewBalance);
        accountService.updateBalance(targetAccount.getId(), targetNewBalance);
        
        // Create outgoing transaction
        Transaction outgoingTransaction = new Transaction();
        outgoingTransaction.setAccountId(sourceAccount.getId());
        outgoingTransaction.setType(TransactionType.TRANSFER_OUT);
        outgoingTransaction.setAmount(amount);
        outgoingTransaction.setTargetAccountId(targetAccountId);
        outgoingTransaction.setDescription(description);
        outgoingTransaction.setTimestamp(LocalDateTime.now());
        
        // Create incoming transaction
        Transaction incomingTransaction = new Transaction();
        incomingTransaction.setAccountId(targetAccount.getId());
        incomingTransaction.setType(TransactionType.TRANSFER_IN);
        incomingTransaction.setAmount(amount);
        incomingTransaction.setTargetAccountId(sourceAccount.getId());
        incomingTransaction.setDescription(description);
        incomingTransaction.setTimestamp(LocalDateTime.now());
        
        Transaction savedOutgoingTransaction = transactionRepository.save(outgoingTransaction);
        transactionRepository.save(incomingTransaction);
        
        return mapToResponse(savedOutgoingTransaction, sourceNewBalance);
    }
    
    public List<TransactionResponse> getTransactionHistory(User user) {
        Account account = accountService.getUserAccount(user);
        List<Transaction> transactions = transactionRepository
                .findByAccountIdOrderByTimestampDesc(account.getId());
        
        BigDecimal currentBalance = account.getBalance();
        List<TransactionResponse> responses = new ArrayList<>();
        
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            // Show current balance only for the most recent transaction
            // For historical transactions, balanceAfter represents what the balance was after that transaction
            BigDecimal balanceAfter = (i == 0) ? currentBalance : null;
            responses.add(mapToResponse(transaction, balanceAfter));
        }
        
        return responses;
    }
    
    private TransactionResponse mapToResponse(Transaction transaction, BigDecimal balanceAfter) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getTargetAccountId(),
                transaction.getDescription(),
                transaction.getTimestamp(),
                balanceAfter
        );
    }
}
