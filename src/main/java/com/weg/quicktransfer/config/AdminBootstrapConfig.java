package com.weg.quicktransfer.config;

import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.repo.AdminRepository;
import com.weg.quicktransfer.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(name = "app.bootstrap.admin.enabled", havingValue = "true")
public class AdminBootstrapConfig {

    @Bean
    ApplicationRunner bootstrapAdmin(
            UserRepository userRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.name:}") String name,
            @Value("${app.bootstrap.admin.username:}") String username,
            @Value("${app.bootstrap.admin.email:}") String email,
            @Value("${app.bootstrap.admin.password:}") String password) {
        return args -> {
            if (userRepository.count() != 0) {
                throw new IllegalStateException(
                        "Admin bootstrap is enabled, but the user table is not empty.");
            }
            if (!StringUtils.hasText(name)
                    || !StringUtils.hasText(username)
                    || !StringUtils.hasText(email)
                    || !isStrongPassword(password)) {
                throw new IllegalStateException(
                        "Admin bootstrap properties are missing or the password is not strong enough.");
            }

            Admin admin = new Admin(name, username, email, passwordEncoder.encode(password));
            adminRepository.save(admin);
        };
    }

    private boolean isStrongPassword(String password) {
        return StringUtils.hasText(password)
                && password.length() >= 14
                && password.chars().anyMatch(Character::isUpperCase)
                && password.chars().anyMatch(Character::isLowerCase)
                && password.chars().anyMatch(Character::isDigit)
                && password.matches(".*[^A-Za-z0-9].*");
    }
}
