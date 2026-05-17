package com.takeout.delivery.controller;

import com.takeout.common.core.annotation.RequireRole;
import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.RoleEnum;
import com.takeout.common.web.util.SecurityUtil;
import com.takeout.delivery.dto.DispatchResultDTO;
import com.takeout.delivery.dto.WebSocketLocationMessage;
import com.takeout.delivery.entity.DeliveryTask;
import com.takeout.delivery.entity.Rider;
import com.takeout.delivery.service.DeliveryTaskService;
import com.takeout.delivery.service.RiderService;
import com.takeout.delivery.websocket.DeliveryWebSocketHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 配送任务控制器
 * 提供：任务查询、抢单、调度、配送追踪等接口
 */
@Slf4j
@RestController
@RequestMapping("/delivery/task")
@RequiredArgsConstructor
public class DeliveryTaskController {

    private final DeliveryTaskService deliveryTaskService;
    private final RiderService riderService;
    private final DeliveryWebSocketHandler webSocketHandler;

    /**
     * 查询可抢单的配送任务列表
     * GET /delivery/task/list
     */
    @GetMapping("/list")
    @RequireRole({RoleEnum.RIDER, RoleEnum.ADMIN})
    public Result<List<DeliveryTask>> listAvailableTasks() {
        List<DeliveryTask> tasks = deliveryTaskService.listAvailableTasks();
        return Result.success(tasks);
    }

    /**
     * 获取配送任务详情
     * GET /delivery/task/{taskNo}
     */
    @GetMapping("/{taskNo}")
    @RequireRole({RoleEnum.RIDER, RoleEnum.ADMIN, RoleEnum.USER})
    public Result<DeliveryTask> getTaskDetail(@PathVariable String taskNo) {
        DeliveryTask task = deliveryTaskService.getTaskDetail(taskNo);
        return Result.success(task);
    }

    /**
     * 骑手抢单
     * POST /delivery/task/{taskNo}/grab
     */
    @PostMapping("/{taskNo}/grab")
    public Result<DispatchResultDTO> grabOrder(HttpServletRequest request,
                                                @PathVariable String taskNo) {
        Long userId = SecurityUtil.getUserId(request);
        Rider rider = riderService.getRiderByUserId(userId);
        if (rider == null) {
            return Result.error(404, "您还未注册为骑手");
        }
        if (!"ONLINE".equals(rider.getStatus())) {
            return Result.error(400, "请先上线再抢单");
        }

        DeliveryTask task = deliveryTaskService.getTaskDetail(taskNo);
        if (!"PENDING".equals(task.getStatus())) {
            return Result.error(400, "该订单已被其他骑手接单");
        }

        // 直接分配给当前骑手
        task.setRiderId(rider.getId());
        task.setRiderName(rider.getName());
        task.setRiderPhone(rider.getPhone());
        task.setStatus("ASSIGNED");
        task.setAssignedAt(LocalDateTime.now());
        deliveryTaskService.updateById(task);

        // 更新骑手状态为忙碌
        riderService.updateRiderStatus(rider.getId(), "BUSY");

        DispatchResultDTO result = DispatchResultDTO.builder()
                .taskNo(task.getTaskNo())
                .riderId(rider.getId())
                .riderName(rider.getName())
                .riderPhone(rider.getPhone())
                .estimatedDistance(task.getEstimatedDistance())
                .estimatedTime(task.getEstimatedTime())
                .status(task.getStatus())
                .build();

        log.info("骑手抢单成功, taskNo: {}, riderId: {}", taskNo, rider.getId());
        return Result.success(result);
    }

    /**
     * 顾客追踪配送（HTTP 查询）
     * GET /delivery/task/track/{orderNo}
     */
    @GetMapping("/track/{orderNo}")
    @RequireRole({RoleEnum.USER, RoleEnum.RIDER, RoleEnum.ADMIN})
    public Result<DeliveryTask> trackDelivery(@PathVariable String orderNo) {
        DeliveryTask task = deliveryTaskService.getTaskByOrderNo(orderNo);
        if (task == null) {
            return Result.error(404, "配送任务不存在");
        }
        return Result.success(task);
    }

    /**
     * 模拟骑手位置推送（用于测试）
     * POST /delivery/task/{taskNo}/simulate-location
     * 实际项目中由骑手客户端通过 WebSocket 上报位置
     */
    @PostMapping("/{taskNo}/simulate-location")
    @RequireRole({RoleEnum.RIDER, RoleEnum.ADMIN})
    public Result<String> simulateLocation(@PathVariable String taskNo,
                                            @RequestParam BigDecimal longitude,
                                            @RequestParam BigDecimal latitude) {
        DeliveryTask task = deliveryTaskService.getTaskDetail(taskNo);

        WebSocketLocationMessage message = WebSocketLocationMessage.builder()
                .type("LOCATION_UPDATE")
                .orderNo(task.getOrderNo())
                .riderId(task.getRiderId())
                .riderName(task.getRiderName())
                .riderPhone(task.getRiderPhone())
                .status(task.getStatus())
                .longitude(longitude)
                .latitude(latitude)
                .estimatedTime(task.getEstimatedTime())
                .timestamp(LocalDateTime.now())
                .build();

        webSocketHandler.pushLocationToSubscribers(task.getOrderNo(), message);
        return Result.success("位置推送成功");
    }
}
