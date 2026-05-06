package com.takeout.user.config;

import com.takeout.user.entity.User;
import com.takeout.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) {
        // Create test account if not exists
        if (userService.getByUsername("root") == null) {
            User user = new User();
            user.setUsername("root");
            user.setPassword("123");
            user.setPhone("13800000000");
            user.setRole("ADMIN");
            user.setStatus(1);
            userService.save(user);
            System.out.println(">>> Test account created: root / 123");
        }
    }
}
