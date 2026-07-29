package com.weg.quicktransfer.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
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

    private static final String DEFAULT_ISSUER = "quick-transfer";

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;
    private final long expirationMs;
    private final String issuer;

    public JwtService(
            @Value("${app.security.jwt.public-key}") RSAPublicKey publicKey,
            @Value("${app.security.jwt.private-key}") RSAPrivateKey privateKey,
            @Value("${app.jwt.expiration}") long expirationMs,
            @Value("${app.jwt.issuer:quick-transfer}") String issuer) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.expirationMs = expirationMs;
        this.issuer = (issuer == null || issuer.isBlank()) ? DEFAULT_ISSUER : issuer;
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Date now = new Date();

        return Jwts.builder()
                .claims(extraClaims)
                .issuer(issuer)
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

            return claims.getSubject() != null
                    && claims.getSubject().equals(userDetails.getUsername())
                    && claims.getIssuer() != null
                    && claims.getIssuer().equals(issuer)
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