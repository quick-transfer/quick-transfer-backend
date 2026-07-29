package com.weg.quicktransfer.security;

import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findFirstByUsername(username)
                .orElse(userRepository.findFirstByName(username)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "User not found with the name/username: " + username
                        )));

        return new UserPrincipal(user);
    }
}
