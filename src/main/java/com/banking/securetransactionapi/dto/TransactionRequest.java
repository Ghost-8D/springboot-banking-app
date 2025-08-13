package com.banking.securetransactionapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Transaction request for deposits and withdrawals")
public class TransactionRequest {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Transaction amount", example = "100.50", minimum = "0.01")
    private BigDecimal amount;
    
    @Schema(description = "Optional transaction description", example = "Salary deposit")
    private String description;
}
