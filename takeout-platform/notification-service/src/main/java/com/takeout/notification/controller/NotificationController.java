package com.takeout.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.common.core.domain.Result;
import com.takeout.common.web.util.SecurityUtil;
import com.takeout.notification.entity.NotificationRecord;
import com.takeout.notification.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知服务控制器
 * 提供：通知记录查询、在线状态查询等接口
 */
@Slf4j
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 查询用户的通知记录
     * GET /notification/list
     */
    @GetMapping("/list")
    public Result<IPage<NotificationRecord>> listNotifications(HttpServletRequest request,
                                                                 @RequestParam(defaultValue = "1") Integer page,
                                                                 @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtil.getUserId(request);
        IPage<NotificationRecord> result = notificationService.listUserNotifications(userId, new Page<>(page, size));
        return Result.success(result);
    }

    /**
     * 查询 WebSocket 在线状态
     * GET /notification/online-status
     * 返回当前在线用户数
     */
    @GetMapping("/online-status")
    public Result<Map<String, Object>> onlineStatus() {
        Map<String, Object> data = new HashMap<>();
        data.put("onlineCount", notificationService.getOnlineUserCount());
        return Result.success(data);
    }
}
