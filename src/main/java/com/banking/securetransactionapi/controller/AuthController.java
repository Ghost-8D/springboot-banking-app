package com.banking.securetransactionapi.controller;

import com.banking.securetransactionapi.dto.AuthResponse;
import com.banking.securetransactionapi.dto.ErrorResponse;
import com.banking.securetransactionapi.dto.LoginRequest;
import com.banking.securetransactionapi.dto.RegisterRequest;
import com.banking.securetransactionapi.exception.UsernameAlreadyExistsException;
import com.banking.securetransactionapi.entity.Account;
import com.banking.securetransactionapi.entity.Role;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.repository.AccountRepository;
import com.banking.securetransactionapi.service.UserService;
import com.banking.securetransactionapi.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with an associated bank account initialized with zero balance"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "User registered successfully", 
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Username already exists or validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        
        User savedUser = userService.save(user);
        
        // Create account for the user
        Account account = new Account();
        account.setUserId(savedUser.getId());
        account.setBalance(BigDecimal.ZERO);
        accountRepository.save(account);
        
        String jwt = jwtUtil.generateToken(savedUser);
        
        return ResponseEntity.ok(new AuthResponse(
                jwt,
                savedUser.getUsername(),
                savedUser.getRole().name()
        ));
    }
    
    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user credentials and return JWT token for subsequent API calls"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Login successful", 
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid credentials or validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        User user = userService.findByUsername(request.getUsername());
        String jwt = jwtUtil.generateToken(user);
        
        return ResponseEntity.ok(new AuthResponse(
                jwt,
                user.getUsername(),
                user.getRole().name()
        ));
    }
}
