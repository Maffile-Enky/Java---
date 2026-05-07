package com.takeout.user.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.ErrorCode;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.common.security.util.PasswordUtil;
import com.takeout.user.entity.User;
import com.takeout.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(@RequestHeader("X-User-Id") Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("phone", user.getPhone() != null ? user.getPhone() : "");
        profile.put("nickname", user.getNickname() != null ? user.getNickname() : "");
        profile.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
        profile.put("role", user.getRole());
        profile.put("createdAt", user.getCreatedAt());
        return Result.success(profile);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestHeader("X-User-Id") Long userId,
                                      @RequestBody Map<String, String> body) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (body.containsKey("nickname")) {
            user.setNickname(body.get("nickname"));
        }
        if (body.containsKey("avatar")) {
            user.setAvatar(body.get("avatar"));
        }
        if (body.containsKey("phone")) {
            user.setPhone(body.get("phone"));
        }
        userService.updateById(user);
        return Result.success(null);
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestHeader("X-User-Id") Long userId,
                                       @RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            throw new BusinessException(400, "请输入原密码和新密码");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }
        user.setPassword(PasswordUtil.encode(newPassword));
        userService.updateById(user);
        return Result.success(null);
    }
}
