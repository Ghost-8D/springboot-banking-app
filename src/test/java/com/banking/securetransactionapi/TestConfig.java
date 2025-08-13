package com.banking.securetransactionapi;

import com.banking.securetransactionapi.entity.Role;
import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.util.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestConfig {
    
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
    
    @Bean
    @Primary
    public UserDetailsService testUserDetailsService() {
        // This prevents the conflict between InMemoryUserDetailsManager and UserService
        return new InMemoryUserDetailsManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public User testUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        return user;
    }
}