package com.weg.quicktransfer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.security.jwt.public-key}") RSAPublicKey publicKey,
            @Value("${app.security.jwt.private-key}") RSAPrivateKey privateKey,
            @Value("${app.jwt.expiration}") long expirationMs) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        var now = new Date();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(privateKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject().equals(userDetails.getUsername())
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}