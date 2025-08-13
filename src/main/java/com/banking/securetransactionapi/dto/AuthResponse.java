package com.banking.securetransactionapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Authentication response containing JWT token and user details")
public class AuthResponse {
    @Schema(description = "JWT token for API authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Username of the authenticated user", example = "john_doe")
    private String username;
    
    @Schema(description = "User role", example = "ROLE_USER")
    private String role;
}
