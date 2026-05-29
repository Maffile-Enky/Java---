package com.takeout.user.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.ErrorCode;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.common.security.util.JwtUtil;
import com.takeout.common.security.util.PasswordUtil;
import com.takeout.user.entity.User;
import com.takeout.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Value("${file.upload-dir:./uploads/avatars}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8081}")
    private String baseUrl;

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

    @PutMapping("/upgrade-role")
    public Result<Map<String, Object>> upgradeRole(@RequestHeader("X-User-Id") Long userId,
                                                    @RequestBody Map<String, String> body) {
        String targetRole = body.get("role");
        if (!"MERCHANT".equals(targetRole) && !"RIDER".equals(targetRole)) {
            throw new BusinessException(400, "只能升级为商家或骑手");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        user.setRole(targetRole);
        userService.updateById(user);

        String newToken = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", newToken);
        data.put("userInfo", buildProfile(user));
        return Result.success(data);
    }

    private Map<String, Object> buildProfile(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("phone", user.getPhone() != null ? user.getPhone() : "");
        info.put("nickname", user.getNickname() != null ? user.getNickname() : "");
        info.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
        info.put("role", user.getRole());
        return info;
    }

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestHeader("X-User-Id") Long userId,
                                                     @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(400, "请选择要上传的文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(400, "文件名不能为空");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".png".equals(ext) && !".gif".equals(ext) && !".webp".equals(ext)) {
            throw new BusinessException(400, "仅支持 jpg/jpeg/png/gif/webp 格式");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(400, "文件大小不能超过5MB");
        }

        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        String datePath = java.time.LocalDate.now().toString().replace("-", "/");
        File destDir = new File(uploadDir + "/" + datePath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try {
            File dest = new File(destDir, filename);
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败");
        }

        String avatarUrl = baseUrl + "/uploads/avatars/" + datePath + "/" + filename;

        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        user.setAvatar(avatarUrl);
        userService.updateById(user);

        Map<String, String> result = new HashMap<>();
        result.put("url", avatarUrl);
        return Result.success(result);
    }
}
