package com.banking.securetransactionapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "User login request")
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Schema(description = "Username", example = "john_doe")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "password123")
    private String password;
}
