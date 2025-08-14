package com.example.relatives.config;

import com.example.relatives.model.Role;
import com.example.relatives.model.User;
import com.example.relatives.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("1");
                admin.setPassword(passwordEncoder.encode("1")); // 🔑 пароль по умолчанию
                admin.setRole(Role.ADMIN);
                admin.setOwner(null); // админ — корневой пользователь
                userRepository.save(admin);
                System.out.println("✅ Создан администратор: admin / admin");
            }
        };
    }
}
