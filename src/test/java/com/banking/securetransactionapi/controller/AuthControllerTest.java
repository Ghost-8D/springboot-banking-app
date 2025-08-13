package com.banking.securetransactionapi.controller;

import com.banking.securetransactionapi.dto.LoginRequest;
import com.banking.securetransactionapi.dto.RegisterRequest;
import com.banking.securetransactionapi.entity.Account;
import com.banking.securetransactionapi.entity.Role;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.repository.AccountRepository;
import com.banking.securetransactionapi.service.UserService;
import com.banking.securetransactionapi.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.banking.securetransactionapi.TestConfig;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@WebMvcTest(controllers = AuthController.class, 
    excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(TestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.ROLE_USER);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_WithValidRequest_ShouldReturnAuthResponse() throws Exception {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.save(any(User.class))).thenReturn(testUser);
        when(accountRepository.save(any(Account.class))).thenReturn(new Account());
        when(jwtUtil.generateToken(testUser)).thenReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        verify(userService).existsByUsername("testuser");
        verify(passwordEncoder).encode("password123");
        verify(userService).save(any(User.class));
        verify(accountRepository).save(any(Account.class));
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    void register_WithExistingUsername_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("USERNAME_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Username 'testuser' is already taken"));

        verify(userService).existsByUsername("testuser");
        verify(userService, never()).save(any(User.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void register_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername(""); // Invalid empty username
        invalidRequest.setPassword("123"); // Invalid short password

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).existsByUsername(anyString());
        verify(userService, never()).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // Successful authentication
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.generateToken(testUser)).thenReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).findByUsername("testuser");
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).findByUsername(anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void login_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername(""); // Invalid empty username
        invalidRequest.setPassword(""); // Invalid empty password

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
        verify(userService, never()).findByUsername(anyString());
    }
}
