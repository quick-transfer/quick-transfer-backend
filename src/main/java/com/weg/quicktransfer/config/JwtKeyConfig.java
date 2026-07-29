package com.weg.quicktransfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtKeyConfig {

    @Value("${app.security.jwt.public-key}")
    private RSAPublicKey publicKey;

    @Value("${app.security.jwt.private-key}")
    private RSAPrivateKey privateKey;

    @Bean
    public RSAPublicKey rsaPublicKey() {
        return publicKey;
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey() {
        return privateKey;
    }
}