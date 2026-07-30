package com.weg.quicktransfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtKeyConfig {

    @Value("${app.security.jwt.public-key}")
    private String publicKey;

    @Value("${app.security.jwt.private-key}")
    private String privateKey;

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String cleanedKey = cleanKey(publicKey, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
        byte[] decode = Base64.getDecoder().decode(cleanedKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        String cleanedKey = cleanKey(privateKey, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
        byte[] decode = Base64.getDecoder().decode(cleanedKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private String cleanKey(String rawKey, String beginHeader, String endHeader) {
        if (rawKey == null) {
            throw new IllegalArgumentException("JWT key must not be null");
        }
        return rawKey
                .replace("\\n", "")
                .replace("\n", "")
                .replace(beginHeader, "")
                .replace(endHeader, "")
                .replaceAll("\\s+", "");
    }
}