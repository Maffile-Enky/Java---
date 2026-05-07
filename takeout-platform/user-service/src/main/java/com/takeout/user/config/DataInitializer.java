package com.takeout.user.config;

import com.takeout.common.security.util.PasswordUtil;
import com.takeout.user.entity.User;
import com.takeout.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        // Create admin account if not exists (with BCrypt hashed password)
        if (userService.getByUsername("root") == null) {
            User user = new User();
            user.setUsername("root");
            user.setPassword(PasswordUtil.encode("123"));
            user.setPhone("13800000000");
            user.setNickname("管理员");
            user.setRole("ADMIN");
            user.setStatus(1);
            userService.save(user);
            System.out.println(">>> Admin account created: root / 123 (BCrypt hashed)");
        } else {
            // Migrate existing plain-text password to BCrypt if needed
            User admin = userService.getByUsername("root");
            if (admin.getPassword() != null && !admin.getPassword().startsWith("$2a$")) {
                admin.setPassword(PasswordUtil.encode(admin.getPassword()));
                userService.updateById(admin);
                System.out.println(">>> Migrated admin password to BCrypt");
            }
        }
    }
}
