package com.banking.securetransactionapi.service;

import com.banking.securetransactionapi.entity.Role;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole(Role.ROLE_USER);
    }

    @Test
    void loadUserByUsername_WithExistingUser_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent");
        });

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void existsByUsername_WithExistingUser_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean result = userService.existsByUsername("testuser");

        // Assert
        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void existsByUsername_WithNonExistingUser_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        boolean result = userService.existsByUsername("nonexistent");

        // Assert
        assertFalse(result);
        verify(userRepository).existsByUsername("nonexistent");
    }
}
