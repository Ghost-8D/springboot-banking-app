package com.banking.securetransactionapi.repository;

import com.banking.securetransactionapi.entity.Role;
import com.banking.securetransactionapi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_WithExistingUser_ShouldReturnUser() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> result = userRepository.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals(Role.ROLE_USER, result.get().getRole());
    }

    @Test
    void findByUsername_WithNonExistingUser_ShouldReturnEmpty() {
        // Act
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void existsByUsername_WithExistingUser_ShouldReturnTrue() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        entityManager.persistAndFlush(user);

        // Act
        boolean result = userRepository.existsByUsername("testuser");

        // Assert
        assertTrue(result);
    }
}
