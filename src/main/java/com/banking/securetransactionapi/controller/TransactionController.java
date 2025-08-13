package com.banking.securetransactionapi.controller;

import com.banking.securetransactionapi.dto.ErrorResponse;
import com.banking.securetransactionapi.dto.TransactionRequest;
import com.banking.securetransactionapi.dto.TransactionResponse;
import com.banking.securetransactionapi.dto.TransferRequest;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for managing financial transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping("/deposit")
    @Operation(
        summary = "Deposit money",
        description = "Deposits money into the authenticated user's account",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Deposit transaction details",
            content = @Content(
                schema = @Schema(implementation = TransactionRequest.class),
                examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Salary Deposit",
                        summary = "Monthly salary deposit",
                        value = "{\"amount\": 2500.00, \"description\": \"Monthly salary deposit\"}"
                    ),
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Cash Deposit",
                        summary = "Cash deposit at branch",
                        value = "{\"amount\": 500.00, \"description\": \"Cash deposit at Main Branch\"}"
                    ),
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Check Deposit",
                        summary = "Check deposit via mobile app",
                        value = "{\"amount\": 750.50, \"description\": \"Check deposit - mobile app\"}"
                    ),
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Refund",
                        summary = "Refund credit",
                        value = "{\"amount\": 89.99, \"description\": \"Product refund credit\"}"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Deposit successful", 
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid amount or validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", 
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        TransactionResponse response = transactionService.deposit(
                user, 
                request.getAmount(), 
                request.getDescription()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/withdraw")
    @Operation(
        summary = "Withdraw money",
        description = "Withdraws money from the authenticated user's account",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Withdrawal transaction details",
            content = @Content(
                schema = @Schema(implementation = TransactionRequest.class),
                examples = {
                    @ExampleObject(
                        name = "ATM Withdrawal",
                        summary = "Cash withdrawal from ATM",
                        value = "{\"amount\": 200.00, \"description\": \"ATM withdrawal - Main Street\"}"
                    ),
                    @ExampleObject(
                        name = "Branch Withdrawal",
                        summary = "Cash withdrawal at bank branch",
                        value = "{\"amount\": 1000.00, \"description\": \"Cash withdrawal - Downtown Branch\"}"
                    ),
                    @ExampleObject(
                        name = "Online Payment",
                        summary = "Online bill payment withdrawal",
                        value = "{\"amount\": 125.50, \"description\": \"Online payment - Electric bill\"}"
                    ),
                    @ExampleObject(
                        name = "Purchase",
                        summary = "Debit card purchase",
                        value = "{\"amount\": 45.75, \"description\": \"Debit card purchase - Grocery Store\"}"
                    ),
                    @ExampleObject(
                        name = "Transfer Fee",
                        summary = "Service fee withdrawal",
                        value = "{\"amount\": 5.00, \"description\": \"Wire transfer fee\"}"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Withdrawal successful", 
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Insufficient funds or invalid amount",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", 
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        TransactionResponse response = transactionService.withdraw(
                user, 
                request.getAmount(), 
                request.getDescription()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/transfer")
    @Operation(
        summary = "Transfer money",
        description = "Transfers money from the authenticated user's account to another account",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Transfer transaction details",
            content = @Content(
                schema = @Schema(implementation = TransferRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Friend Payment",
                        summary = "Send money to friend",
                        value = "{\"amount\": 50.00, \"targetAccountId\": 2, \"description\": \"Dinner payment - Thanks for covering!\"}"
                    ),
                    @ExampleObject(
                        name = "Family Transfer",
                        summary = "Transfer to family member",
                        value = "{\"amount\": 300.00, \"targetAccountId\": 3, \"description\": \"Monthly allowance transfer\"}"
                    ),
                    @ExampleObject(
                        name = "Business Payment",
                        summary = "Payment for services",
                        value = "{\"amount\": 750.00, \"targetAccountId\": 4, \"description\": \"Payment for consulting services\"}"
                    ),
                    @ExampleObject(
                        name = "Rent Payment",
                        summary = "Monthly rent transfer",
                        value = "{\"amount\": 1200.00, \"targetAccountId\": 5, \"description\": \"Monthly rent payment - April 2024\"}"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Transfer successful", 
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Insufficient funds or invalid transfer (e.g., same account)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Target account not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", 
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        TransactionResponse response = transactionService.transfer(
                user,
                request.getTargetAccountId(),
                request.getAmount(),
                request.getDescription()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    @Operation(
        summary = "Get transaction history",
        description = "Retrieves the transaction history for the authenticated user's account"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Transaction history retrieved successfully"),
        @ApiResponse(responseCode = "401", 
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        List<TransactionResponse> history = transactionService.getTransactionHistory(user);
        
        return ResponseEntity.ok(history);
    }
}
