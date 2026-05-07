package com.takeout.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.common.core.annotation.RequireRole;
import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.ErrorCode;
import com.takeout.common.core.enums.RoleEnum;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.user.entity.MerchantApplication;
import com.takeout.user.entity.User;
import com.takeout.user.service.MerchantApplicationService;
import com.takeout.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/admin")
@RequireRole(RoleEnum.ADMIN)
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;
    private final MerchantApplicationService applicationService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.count());
        stats.put("totalMerchants", userService.count(
                new LambdaQueryWrapper<User>().eq(User::getRole, "MERCHANT")));
        stats.put("pendingApplications", applicationService.count(
                new LambdaQueryWrapper<MerchantApplication>().eq(MerchantApplication::getStatus, 0)));
        return Result.success(stats);
    }

    @GetMapping("/user/list")
    public Result<IPage<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<User> result = userService.listUsers(new Page<>(page, size), keyword);
        // Clear passwords in response
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    @PutMapping("/user/status/{id}")
    public Result<Boolean> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return Result.success(userService.updateById(user));
    }

    @PutMapping("/user/role/{id}")
    public Result<Boolean> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return Result.success(userService.updateById(user));
    }

    @GetMapping("/merchant-application/list")
    public Result<IPage<MerchantApplication>> listApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<MerchantApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(MerchantApplication::getStatus, status);
        }
        wrapper.orderByDesc(MerchantApplication::getCreatedAt);
        IPage<MerchantApplication> result = applicationService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    @PutMapping("/merchant-application/approve/{id}")
    @Transactional
    public Result<Boolean> approveApplication(@PathVariable Long id) {
        MerchantApplication app = applicationService.getById(id);
        if (app == null) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_FOUND);
        }
        if (app.getStatus() != 0) {
            throw new BusinessException(ErrorCode.APPLICATION_ALREADY_PROCESSED);
        }
        // Update application status
        app.setStatus(1);
        applicationService.updateById(app);
        // Change user role to MERCHANT
        User user = userService.getById(app.getUserId());
        if (user != null) {
            user.setRole("MERCHANT");
            userService.updateById(user);
        }
        return Result.success(true);
    }

    @PutMapping("/merchant-application/reject/{id}")
    public Result<Boolean> rejectApplication(@PathVariable Long id, @RequestBody Map<String, String> body) {
        MerchantApplication app = applicationService.getById(id);
        if (app == null) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_FOUND);
        }
        if (app.getStatus() != 0) {
            throw new BusinessException(ErrorCode.APPLICATION_ALREADY_PROCESSED);
        }
        app.setStatus(2);
        app.setAdminNote(body.get("adminNote"));
        applicationService.updateById(app);
        return Result.success(true);
    }
}
