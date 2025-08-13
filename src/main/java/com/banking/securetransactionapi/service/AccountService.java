package com.banking.securetransactionapi.service;

import com.banking.securetransactionapi.entity.Account;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.exception.AccountNotFoundException;
import com.banking.securetransactionapi.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public Account findByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user"));
    }
    
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }
    
    public Account updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = findById(accountId);
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
    
    public boolean hasSufficientBalance(Long accountId, BigDecimal amount) {
        Account account = findById(accountId);
        return account.getBalance().compareTo(amount) >= 0;
    }
    
    public Account getUserAccount(User user) {
        return findByUserId(user.getId());
    }
}
