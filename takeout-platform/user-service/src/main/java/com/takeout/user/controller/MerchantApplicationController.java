package com.takeout.user.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.ErrorCode;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.user.entity.MerchantApplication;
import com.takeout.user.entity.User;
import com.takeout.user.service.MerchantApplicationService;
import com.takeout.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/merchant-application")
@RequiredArgsConstructor
public class MerchantApplicationController {

    private final MerchantApplicationService applicationService;
    private final UserService userService;

    @PostMapping
    public Result<Boolean> apply(@RequestHeader("X-User-Id") Long userId,
                                 @RequestBody MerchantApplication application) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if ("MERCHANT".equals(user.getRole()) || "ADMIN".equals(user.getRole())) {
            throw new BusinessException(ErrorCode.ALREADY_MERCHANT);
        }
        // Check for pending application
        MerchantApplication existing = applicationService.getLatestByUserId(userId);
        if (existing != null && existing.getStatus() == 0) {
            throw new BusinessException(ErrorCode.APPLICATION_ALREADY_EXISTS);
        }

        application.setUserId(userId);
        application.setStatus(0);
        application.setId(null);
        return Result.success(applicationService.save(application));
    }

    @GetMapping("/status")
    public Result<MerchantApplication> status(@RequestHeader("X-User-Id") Long userId) {
        MerchantApplication app = applicationService.getLatestByUserId(userId);
        return Result.success(app);
    }
}
