package org.example.messmate;

import org.example.messmate.entity.User;
import org.example.messmate.enums.Role;
import org.example.messmate.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String username;

    @Value("${app.admin.password:Admin@1234}")
    private String password;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
    }

    private void seedAdminUser() {
        if(userRepository.findByUsername(username).isEmpty()) {
            User admin = new User();

            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            admin.setVerified(true);
            
            userRepository.save(admin);

            log.info("Default admin user '{}' successfully created", username);
        }
        else {
            log.info("Admin user '{}' already exists", username);
        }
    }
}
