package com.takeout.user.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.common.security.util.JwtUtil;
import com.takeout.user.entity.User;
import com.takeout.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return Result.error(400, "用户名和密码不能为空");
        }

        User user = userService.getByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return Result.error(401, "用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            return Result.error(403, "账户已被禁用");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> data = Map.of(
                "token", token,
                "userInfo", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "phone", user.getPhone() != null ? user.getPhone() : ""
                )
        );

        return Result.success(data);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole(),
                "phone", user.getPhone() != null ? user.getPhone() : ""
        ));
    }
}
