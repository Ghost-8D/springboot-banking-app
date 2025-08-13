package com.banking.securetransactionapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Transfer request for money transfers between accounts")
public class TransferRequest {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Transfer amount", example = "250.00", minimum = "0.01")
    private BigDecimal amount;
    
    @NotNull(message = "Target account ID is required")
    @Schema(description = "ID of the target account to transfer money to", example = "2")
    private Long targetAccountId;
    
    @Schema(description = "Optional transfer description", example = "Payment for services")
    private String description;
}
