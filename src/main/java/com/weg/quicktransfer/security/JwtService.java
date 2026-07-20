package com.weg.quicktransfer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Marks this class as a Spring service bean to manage JWT creation and validation
@Service
public class JwtService {

    private final String secret;                                                              // Base64-encoded secret key injected from application properties
    private final long expirationMs;                                                          // Token expiration duration in milliseconds injected from properties

    // Constructor to initialize configuration parameters via Spring's @Value annotation
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    // Generates a standard JWT token containing only the default claims
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generates a custom JWT token containing additional user-defined claims
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        var now = new Date();                                                                 // Captures the exact creation timestamp
        return Jwts.builder()
                .claims(extraClaims)                                                          // Attaches custom payload properties
                .subject(userDetails.getUsername())                                           // Sets the username as the subject of the token
                .issuedAt(now)                                                                // Sets the token generation timestamp
                .expiration(new Date(now.getTime() + expirationMs))                           // Sets the dynamic expiration time window
                .signWith(getSigningKey())                                                    // Cryptographically signs the token using the HMAC key
                .compact();                                                                   // Serializes the JWT to its compact, URL-safe string format
    }

    // Extracts the subject/username property from the token payload
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Validates whether the token matches the user details and isn't expired
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = extractAllClaims(token);                                          // Decodes the token to access its structural claims
            return claims.getSubject().equals(userDetails.getUsername())                      // Verifies that the token owner matches the user entity
                    && claims.getExpiration() != null                                         // Ensures an expiration parameter exists
                    && claims.getExpiration().after(new Date());                              // Evaluates if the token is still active (not expired)
        } catch (ExpiredJwtException e) {
            return false;                                                                     // Returns false if the token has already expired
        } catch (JwtException | IllegalArgumentException e) {
            return false;                                                                     // Returns false for structural errors or invalid formats
        }
    }

    // Decrypts and retrieves the full claims payload using the verification key
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Converts the raw configuration secret into a secure cryptographic HMAC key
    private SecretKey getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);                  // Decodes the configuration string into a raw byte array
        return Keys.hmacShaKeyFor(keyBytes);                                                  // Generates a secure HMAC-SHA key object
    }
}