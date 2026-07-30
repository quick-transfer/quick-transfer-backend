package com.weg.quicktransfer.config;

import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.AdminRepository;
import com.weg.quicktransfer.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User user = userRepository.findFirstByUsername("Guilherme Doge")
                .orElseGet(() -> userRepository.findFirstByName("Guilherme Doge").orElse(null));

        if (user == null) {
            log.info("Creating default admin user 'Guilherme Doge'...");
            Admin admin = new Admin(
                    "Guilherme Doge",
                    "Guilherme Doge",
                    "guilherme.doge@weg.net",
                    passwordEncoder.encode("Admin@1234567890")
            );
            admin.setFirstLogin(false);
            adminRepository.save(admin);
            log.info("Default admin user created successfully.");
        } else {
            log.info("Ensuring admin user 'Guilherme Doge' is ready for login...");
            user.setPassword(passwordEncoder.encode("Admin@1234567890"));
            user.setFirstLogin(false);
            userRepository.save(user);
            log.info("Admin user 'Guilherme Doge' updated successfully.");
        }
    }
}
