package com.banking.securetransactionapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "target_account_id")
    private Long targetAccountId; // For transfers
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;
}
