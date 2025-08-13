package com.banking.securetransactionapi.controller;

import com.banking.securetransactionapi.dto.ErrorResponse;
import com.banking.securetransactionapi.entity.Account;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "Endpoints for managing user accounts and checking balances")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {
    
    private final AccountService accountService;
    
    @GetMapping("/balance")
    @Operation(
        summary = "Get account balance",
        description = "Retrieves the current balance for the authenticated user's account"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Balance retrieved successfully"),
        @ApiResponse(responseCode = "401", 
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Account not found for user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, Object>> getBalance(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Account account = accountService.getUserAccount(user);
        
        return ResponseEntity.ok(Map.of(
                "accountId", account.getId(),
                "balance", account.getBalance(),
                "username", user.getUsername()
        ));
    }
}
