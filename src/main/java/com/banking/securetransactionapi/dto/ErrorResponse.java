package com.banking.securetransactionapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response containing details about the error")
public class ErrorResponse {
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Error type/category", example = "INSUFFICIENT_FUNDS")
    private String error;
    
    @Schema(description = "Human-readable error message", example = "Insufficient balance for withdrawal")
    private String message;
    
    @Schema(description = "Detailed error description for developers", example = "Account balance: $50.00, attempted withdrawal: $100.00")
    private String details;
    
    @Schema(description = "API endpoint path", example = "/api/transactions/withdraw")
    private String path;
    
    @Schema(description = "Timestamp when error occurred", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(int status, String error, String message, String details, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}