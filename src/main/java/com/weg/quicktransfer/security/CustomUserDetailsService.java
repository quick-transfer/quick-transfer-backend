package com.weg.quicktransfer.security;

import com.weg.quicktransfer.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Marks this class as a Spring service bean to handle user data retrieval
@Service
// Lombok annotation to automatically generate a constructor for final fields
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // Repository interface used to query user records from the database
    private final UserRepository userRepository;

    // Locates a user based on their username to establish their security context
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));       // Throws exception if user does not exist in database

        return new UserPrincipal(user);                                                    // Wraps the domain user inside Spring's UserDetails wrapper
    }
}