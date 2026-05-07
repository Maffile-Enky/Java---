package com.takeout.user.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.ErrorCode;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.common.security.util.JwtUtil;
import com.takeout.common.security.util.PasswordUtil;
import com.takeout.common.security.util.WeChatOAuthUtil;
import com.takeout.common.redis.service.SmsCodeService;
import com.takeout.user.entity.User;
import com.takeout.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final SmsCodeService smsCodeService;

    @Value("${takeout.wechat.appid:}")
    private String wechatAppId;

    @Value("${takeout.wechat.secret:}")
    private String wechatSecret;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String phone = body.get("phone");

        if (username == null || password == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        if (userService.getByUsername(username) != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(password));
        user.setPhone(phone);
        user.setNickname(username);
        user.setRole("USER");
        user.setStatus(1);
        userService.save(user);

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", buildUserInfo(user));
        return Result.success(data);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }

        User user = userService.getByUsername(username);
        if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", buildUserInfo(user));
        return Result.success(data);
    }

    @PostMapping("/login/phone")
    public Result<Map<String, Object>> loginByPhone(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String code = body.get("code");

        if (phone == null || code == null) {
            throw new BusinessException(400, "手机号和验证码不能为空");
        }
        if (!smsCodeService.verifyCode(phone, code)) {
            throw new BusinessException(ErrorCode.USER_SMS_CODE_ERROR);
        }

        User user = userService.getByPhone(phone);
        if (user == null) {
            // Auto-register with phone
            user = new User();
            user.setUsername("phone_" + phone);
            user.setPassword(PasswordUtil.encode(UUID.randomUUID().toString()));
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(phone.length() - 4));
            user.setRole("USER");
            user.setStatus(1);
            userService.save(user);
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", buildUserInfo(user));
        return Result.success(data);
    }

    @PostMapping("/sms/send")
    public Result<Void> sendSmsCode(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(ErrorCode.USER_PHONE_INVALID);
        }
        if (smsCodeService.isRateLimited(phone)) {
            throw new BusinessException(429, "验证码发送过于频繁，请稍后再试");
        }
        // Generate 6-digit code; in production, call SMS provider API
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        smsCodeService.saveCode(phone, code);
        // TODO: integrate with SMS provider (Aliyun SMS, Tencent SMS, etc.)
        // For development, log the code
        System.out.println(">>> SMS code for " + phone + ": " + code);
        return Result.success(null);
    }

    @PostMapping("/login/wechat")
    public Result<Map<String, Object>> loginByWechat(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null) {
            throw new BusinessException(400, "微信授权码不能为空");
        }

        WeChatOAuthUtil.WeChatTokenResult tokenResult =
                WeChatOAuthUtil.getAccessToken(wechatAppId, wechatSecret, code);
        if (tokenResult == null) {
            throw new BusinessException(ErrorCode.USER_WECHAT_AUTH_FAILED);
        }

        User user = userService.getByOpenid(tokenResult.getOpenid());
        if (user == null) {
            // Fetch WeChat user info for registration
            WeChatOAuthUtil.WeChatUserInfo wxInfo =
                    WeChatOAuthUtil.getUserInfo(tokenResult.getAccessToken(), tokenResult.getOpenid());

            user = new User();
            user.setUsername("wx_" + tokenResult.getOpenid().substring(0, 8));
            user.setPassword(PasswordUtil.encode(UUID.randomUUID().toString()));
            user.setOpenid(tokenResult.getOpenid());
            user.setNickname(wxInfo != null ? wxInfo.getNickname() : "微信用户");
            user.setAvatar(wxInfo != null ? wxInfo.getHeadImgUrl() : "");
            user.setRole("USER");
            user.setStatus(1);
            userService.save(user);
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        String jwtToken = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", jwtToken);
        data.put("userInfo", buildUserInfo(user));
        return Result.success(data);
    }

    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        try {
            if (JwtUtil.isTokenExpired(token)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }
            Long userId = JwtUtil.getUserId(token);
            User user = userService.getById(userId);
            if (user == null || user.getStatus() != 1) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }
            String tokenRole = JwtUtil.getRole(token);
            // If role hasn't changed, return the same token
            if (tokenRole != null && tokenRole.equals(user.getRole())) {
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("userInfo", buildUserInfo(user));
                return Result.success(data);
            }
            // Role changed — re-issue token
            String newToken = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("token", newToken);
            data.put("userInfo", buildUserInfo(user));
            return Result.success(data);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return Result.success(buildUserInfo(user));
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("role", user.getRole());
        info.put("phone", user.getPhone() != null ? user.getPhone() : "");
        info.put("nickname", user.getNickname() != null ? user.getNickname() : "");
        info.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
        return info;
    }
}
