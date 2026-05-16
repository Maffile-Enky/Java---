package com.takeout.delivery.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.common.web.util.SecurityUtil;
import com.takeout.delivery.dto.LocationUpdateDTO;
import com.takeout.delivery.entity.DeliveryTask;
import com.takeout.delivery.entity.Rider;
import com.takeout.delivery.entity.RiderLocation;
import com.takeout.delivery.service.DeliveryTaskService;
import com.takeout.delivery.service.RiderLocationService;
import com.takeout.delivery.service.RiderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 骑手控制器
 * 提供：骑手注册、状态管理、位置更新、任务操作等接口
 */
@Slf4j
@RestController
@RequestMapping("/delivery/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;
    private final RiderLocationService riderLocationService;
    private final DeliveryTaskService deliveryTaskService;

    /**
     * 注册骑手
     * POST /delivery/rider/register
     */
    @PostMapping("/register")
    public Result<Rider> registerRider(HttpServletRequest request, @RequestBody Rider rider) {
        Long userId = SecurityUtil.getUserId(request);
        Rider registered = riderService.registerRider(userId, rider);
        return Result.success(registered);
    }

    /**
     * 获取骑手信息
     * GET /delivery/rider/info
     */
    @GetMapping("/info")
    public Result<Rider> getRiderInfo(HttpServletRequest request) {
        Long userId = SecurityUtil.getUserId(request);
        Rider rider = riderService.getRiderByUserId(userId);
        if (rider == null) {
            return Result.error(404, "您还未注册为骑手");
        }
        return Result.success(rider);
    }

    /**
     * 更新骑手状态（上线/下线）
     * PUT /delivery/rider/status?status=ONLINE
     */
    @PutMapping("/status")
    public Result<Rider> updateStatus(HttpServletRequest request,
                                       @RequestParam String status) {
        Long userId = SecurityUtil.getUserId(request);
        Rider rider = riderService.getRiderByUserId(userId);
        if (rider == null) {
            return Result.error(404, "您还未注册为骑手");
        }
        Rider updated = riderService.updateRiderStatus(rider.getId(), status);
        return Result.success(updated);
    }

    /**
     * 更新骑手位置
     * POST /delivery/rider/location
     */
    @PostMapping("/location")
    public Result<String> updateLocation(HttpServletRequest request,
                                          @RequestBody LocationUpdateDTO dto) {
        Long userId = SecurityUtil.getUserId(request);
        Rider rider = riderService.getRiderByUserId(userId);
        if (rider == null) {
            return Result.error(404, "您还未注册为骑手");
        }

        // 更新骑手当前位置
        riderService.updateRiderLocation(rider.getId(), dto.getLongitude(), dto.getLatitude());

        // 记录位置历史
        RiderLocation location = new RiderLocation();
        location.setRiderId(rider.getId());
        location.setLongitude(dto.getLongitude());
        location.setLatitude(dto.getLatitude());
        location.setSpeed(dto.getSpeed());
        location.setHeading(dto.getHeading());
        riderLocationService.recordLocation(location);

        return Result.success("位置更新成功");
    }

    /**
     * 查询骑手的配送任务列表
     * GET /delivery/rider/tasks?status=ASSIGNED
     */
    @GetMapping("/tasks")
    public Result<List<DeliveryTask>> listTasks(HttpServletRequest request,
                                                 @RequestParam(required = false) String status) {
        Long userId = SecurityUtil.getUserId(request);
        Rider rider = riderService.getRiderByUserId(userId);
        if (rider == null) {
            return Result.error(404, "您还未注册为骑手");
        }
        List<DeliveryTask> tasks = deliveryTaskService.listRiderTasks(rider.getId(), status);
        return Result.success(tasks);
    }

    /**
     * 骑手取餐
     * POST /delivery/rider/tasks/{taskNo}/pickup
     */
    @PostMapping("/tasks/{taskNo}/pickup")
    public Result<DeliveryTask> pickUpOrder(HttpServletRequest request,
                                             @PathVariable String taskNo) {
        Long userId = SecurityUtil.getUserId(request);
        Rider rider = riderService.getRiderByUserId(userId);
        if (rider == null) {
            return Result.error(404, "您还未注册为骑手");
        }
        DeliveryTask task = deliveryTaskService.pickUpOrder(rider.getId(), taskNo);
        return Result.success(task);
    }

    /**
     * 骑手送达
     * POST /delivery/rider/tasks/{taskNo}/deliver
     */
    @PostMapping("/tasks/{taskNo}/deliver")
    public Result<DeliveryTask> deliverOrder(HttpServletRequest request,
                                              @PathVariable String taskNo) {
        Long userId = SecurityUtil.getUserId(request);
        Rider rider = riderService.getRiderByUserId(userId);
        if (rider == null) {
            return Result.error(404, "您还未注册为骑手");
        }
        DeliveryTask task = deliveryTaskService.deliverOrder(rider.getId(), taskNo);
        return Result.success(task);
    }
}
