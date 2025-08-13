package com.banking.securetransactionapi.util;

import com.banking.securetransactionapi.entity.Role;
import com.banking.securetransactionapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKey123456789012345678901234567890123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole(Role.ROLE_USER);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        // Act
        String token = jwtUtil.generateToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        // Arrange
        String token = jwtUtil.generateToken(testUser);

        // Act
        String username = jwtUtil.extractUsername(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateToken(testUser);

        // Act
        Boolean isValid = jwtUtil.validateToken(token, testUser);

        // Assert
        assertTrue(isValid);
    }
}
