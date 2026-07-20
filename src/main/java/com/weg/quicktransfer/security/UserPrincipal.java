package com.weg.quicktransfer.security;

import com.weg.quicktransfer.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Custom adapter class that bridges the domain User entity with Spring Security's UserDetails contract
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    // The wrapped core domain user entity containing the application profile data
    private final User user;

    // Maps the user's domain role into an array of Spring Security granted authorities
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));       // Prefixes the role with 'ROLE_' per Spring standards
    }

    // Retrieves the encrypted password string required for authentication checks
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Retrieves the unique login identifier used to verify identity during authentication
    @Override
    public String getUsername() {
        return user.getUserName();
    }

    // Custom helper method to expose the person's real name outside of security scopes
    public String getName() {
        return user.getName();
    }

    // Custom helper method to expose the primary key/ID for database tracking
    public Long getId() {
        return user.getId();
    }

    // Evaluates whether the user's registration account timeline has expired
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();                                        // Uses default interface implementation (returns true)
    }

    // Evaluates whether the user is administrative locked out of their access profile
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();                                         // Uses default interface implementation (returns true)
    }

    // Evaluates whether the user's authentication credentials/password have expired
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();                                    // Uses default interface implementation (returns true)
    }

    // Evaluates whether the user profile is active or completely disabled
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();                                                  // Uses default interface implementation (returns true)
    }
}