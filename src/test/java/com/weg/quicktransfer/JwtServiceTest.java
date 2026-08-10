package com.weg.quicktransfer;

import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.security.JwtService;
import com.weg.quicktransfer.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;
    private Admin admin;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        jwtService = new JwtService(
                (RSAPublicKey) keyPair.getPublic(),
                (RSAPrivateKey) keyPair.getPrivate(),
                60_000,
                "quick-transfer-test");

        admin = Admin.builder()
                .id(UUID.randomUUID())
                .name("Admin")
                .username("admin")
                .email("admin@example.com")
                .password("encoded-password")
                .tokenVersion(0)
                .build();
    }

    @Test
    void tokenIsInvalidatedWhenSessionVersionChanges() {
        UserPrincipal principal = new UserPrincipal(admin);
        String token = jwtService.generateToken(principal);

        assertTrue(jwtService.isTokenValid(token, principal));

        admin.setTokenVersion(1);
        assertFalse(jwtService.isTokenValid(token, new UserPrincipal(admin)));
    }

    @Test
    void malformedTokenIsRejected() {
        assertFalse(jwtService.isTokenValid("not-a-jwt", new UserPrincipal(admin)));
    }
}
