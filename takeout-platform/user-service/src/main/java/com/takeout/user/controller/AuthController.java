package com.takeout.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @PostMapping("/login")
    public Map<String, Object> login() {
        return Map.of("token", "dummy-jwt-token");
    }
}