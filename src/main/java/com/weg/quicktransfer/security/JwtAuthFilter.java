package com.weg.quicktransfer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Marks this filter as a Spring-managed component to be injected into the security chain
@Component
// Lombok annotation to automatically generate a constructor for final fields
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    // Service containing utility methods for parsing, validating, and extracting JWT data
    private final JwtService jwtService;
    // Core service used to load user details from the database based on the JWT subject
    private final UserDetailsService userDetailsService;

    // Intercepts every incoming HTTP request exactly once to process potential JWT credentials
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");                                    // Extracts the Authorization header from the request

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {                                // Checks if the header is missing or isn't a Bearer token
            filterChain.doFilter(request, response);                                                  // Passes request to the next filter if no valid header exists
            return;                                                                                   // Exits early to avoid further JWT processing
        }

        String token = authHeader.substring(7);                                            // Extracts the actual JWT token by skipping "Bearer " prefix

        try {
            String username = jwtService.extractUsername(token);                                      // Extracts the username/subject from the token payload

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // Checks if user is present and not already authenticated
                var userDetails = userDetailsService.loadUserByUsername(username);                    // Loads user entity from database using the extracted username

                if (jwtService.isTokenValid(token, userDetails)) {                                    // Verifies token integrity, expiration, and ownership
                    var authToken = new UsernamePasswordAuthenticationToken(                          // Instantiates an authentication token with user privileges
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Attaches request metadata (like IP address) to the token
                    SecurityContextHolder.getContext().setAuthentication(authToken);                  // Establishes the authenticated user context in Spring Security
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();                                                     // Clears context if token processing fails (expired, altered, etc.)
        }

        filterChain.doFilter(request, response);                                                      // Hand off request and response to the next filter in the pipeline
    }
}