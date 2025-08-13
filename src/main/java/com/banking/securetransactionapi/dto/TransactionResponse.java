package com.banking.securetransactionapi.dto;

import com.banking.securetransactionapi.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transaction response with transaction details and updated balance")
public class TransactionResponse {
    @Schema(description = "Transaction ID", example = "1")
    private Long id;
    
    @Schema(description = "Transaction type", example = "DEPOSIT")
    private TransactionType type;
    
    @Schema(description = "Transaction amount", example = "100.50")
    private BigDecimal amount;
    
    @Schema(description = "Target account ID for transfers (null for deposits/withdrawals)", example = "2")
    private Long targetAccountId;
    
    @Schema(description = "Transaction description", example = "Salary deposit")
    private String description;
    
    @Schema(description = "Transaction timestamp", example = "2023-12-01T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Account balance after the transaction", example = "1500.75")
    private BigDecimal balanceAfter;
}
