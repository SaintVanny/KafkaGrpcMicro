package com.vanna.orders_serviceApp.config;

import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import com.vanna.orders_serviceApp.repository.UserRepository;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createDefaultAdmin();
    }

    /**
     * default admin
     * Username: admin
     * Password: admin
     */
    private void createDefaultAdmin() {
        String adminName = "admin";

        if (userRepository.findByUsername(adminName).isEmpty()) {
            User admin = User.builder()
                    .username(adminName)
                    .password(passwordEncoder.encode("admin"))
                    .role(UserRole.ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("Default ADMIN user created: username='admin', password='admin'");
        } else {
            log.info("ADMIN user already exists");
        }
    }
}