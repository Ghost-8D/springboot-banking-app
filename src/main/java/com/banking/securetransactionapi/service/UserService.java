package com.banking.securetransactionapi.service;

import com.banking.securetransactionapi.entity.User;
import com.banking.securetransactionapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
}
