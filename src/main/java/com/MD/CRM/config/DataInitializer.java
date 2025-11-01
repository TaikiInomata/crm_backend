package com.MD.CRM.config;

import com.MD.CRM.entity.User;
import com.MD.CRM.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;

    @Bean
    CommandLineRunner initDefaultUser() {
        return args -> {
            String defaultEmail = "admin@crm.com";
            String defaultPassword = "admin123";

            // ✅ Kiểm tra nếu chưa có admin thì mới tạo
            if (userRepository.findByEmailAndIsActiveTrue(defaultEmail).isEmpty()) {

                // ✅ Mã hóa mật khẩu bằng jBCrypt
                String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt(10));

                User admin = User.builder()
                        .username("admin")
                        .email(defaultEmail)
                        .password(hashedPassword)
                        .fullname("System Administrator")
                        .role(User.Role.ADMIN)
                        .isActive(true)
                        .build();

                userRepository.save(admin);

                System.out.println("✅ Default admin user created successfully!");
                System.out.println("   Email: " + defaultEmail);
                System.out.println("   Password: " + defaultPassword);
            } else {
                System.out.println("ℹ️ Default admin user already exists, skipping creation.");
            }
        };
    }
}
