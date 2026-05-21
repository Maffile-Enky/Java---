package com.takeout.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.delivery.dto.DeliveryTaskCreateDTO;
import com.takeout.delivery.dto.DispatchResultDTO;
import com.takeout.delivery.entity.DeliveryTask;
import com.takeout.delivery.entity.Rider;
import com.takeout.delivery.mapper.DeliveryTaskMapper;
import com.takeout.delivery.service.DeliveryTaskService;
import com.takeout.delivery.service.DispatchService;
import com.takeout.delivery.service.RiderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 配送任务服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryTaskServiceImpl extends ServiceImpl<DeliveryTaskMapper, DeliveryTask>
        implements DeliveryTaskService {

    private final DispatchService dispatchService;
    private final RiderService riderService;

    @Override
    @Transactional
    public DeliveryTask createTask(DeliveryTaskCreateDTO dto) {
        DeliveryTask task = new DeliveryTask();
        task.setTaskNo(generateTaskNo());
        task.setOrderId(dto.getOrderId());
        task.setOrderNo(dto.getOrderNo());
        task.setMerchantId(dto.getMerchantId());
        task.setMerchantName(dto.getMerchantName());
        task.setMerchantAddress(dto.getMerchantAddress());
        task.setMerchantLongitude(dto.getMerchantLongitude());
        task.setMerchantLatitude(dto.getMerchantLatitude());
        task.setDeliveryAddress(dto.getDeliveryAddress());
        task.setDeliveryLongitude(dto.getDeliveryLongitude());
        task.setDeliveryLatitude(dto.getDeliveryLatitude());
        task.setDeliveryPhone(dto.getDeliveryPhone());
        task.setDeliveryName(dto.getDeliveryName());
        task.setFee(dto.getFee());
        task.setNote(dto.getNote());
        task.setStatus("PENDING");

        // 计算预估距离和时间
        if (dto.getMerchantLongitude() != null && dto.getMerchantLatitude() != null
                && dto.getDeliveryLongitude() != null && dto.getDeliveryLatitude() != null) {
            BigDecimal distance = dispatchService.calculateDistance(
                    dto.getMerchantLongitude(), dto.getMerchantLatitude(),
                    dto.getDeliveryLongitude(), dto.getDeliveryLatitude());
            task.setEstimatedDistance(distance);
            task.setEstimatedTime(dispatchService.estimateDeliveryTime(distance));
        }

        save(task);
        log.info("配送任务创建成功, taskNo: {}, orderNo: {}", task.getTaskNo(), task.getOrderNo());
        return task;
    }

    @Override
    @Transactional
    public DispatchResultDTO dispatchOrder(String taskNo) {
        DeliveryTask task = getTaskByNo(taskNo);

        if (!"PENDING".equals(task.getStatus())) {
            throw new BusinessException(400, "任务状态不允许调度");
        }

        // 查找最近的骑手
        Long riderId = dispatchService.findNearestRider(task.getMerchantLongitude(), task.getMerchantLatitude());
        if (riderId == null) {
            throw new BusinessException(503, "暂无可用骑手，请稍后重试");
        }

        Rider rider = riderService.getRiderById(riderId);

        // 分配骑手
        task.setRiderId(rider.getId());
        task.setRiderName(rider.getName());
        task.setRiderPhone(rider.getPhone());
        task.setStatus("ASSIGNED");
        task.setAssignedAt(LocalDateTime.now());
        updateById(task);

        // 更新骑手状态为忙碌
        riderService.updateRiderStatus(rider.getId(), "BUSY");

        log.info("配送任务已分配, taskNo: {}, riderId: {}", taskNo, rider.getId());

        return DispatchResultDTO.builder()
                .taskNo(task.getTaskNo())
                .riderId(rider.getId())
                .riderName(rider.getName())
                .riderPhone(rider.getPhone())
                .estimatedDistance(task.getEstimatedDistance())
                .estimatedTime(task.getEstimatedTime())
                .status(task.getStatus())
                .build();
    }

    @Override
    @Transactional
    public DeliveryTask pickUpOrder(Long riderId, String taskNo) {
        DeliveryTask task = getTaskByNo(taskNo);

        if (!"ASSIGNED".equals(task.getStatus())) {
            throw new BusinessException(400, "任务状态不允许取餐");
        }
        if (!riderId.equals(task.getRiderId())) {
            throw new BusinessException(403, "无权操作此配送任务");
        }

        task.setStatus("PICKED_UP");
        task.setPickedUpAt(LocalDateTime.now());
        updateById(task);

        log.info("骑手已取餐, taskNo: {}, riderId: {}", taskNo, riderId);
        return task;
    }

    @Override
    @Transactional
    public DeliveryTask deliverOrder(Long riderId, String taskNo) {
        DeliveryTask task = getTaskByNo(taskNo);

        if (!"PICKED_UP".equals(task.getStatus()) && !"DELIVERING".equals(task.getStatus())) {
            throw new BusinessException(400, "任务状态不允许送达");
        }
        if (!riderId.equals(task.getRiderId())) {
            throw new BusinessException(403, "无权操作此配送任务");
        }

        task.setStatus("COMPLETED");
        task.setDeliveredAt(LocalDateTime.now());

        // 计算实际时间
        if (task.getPickedUpAt() != null) {
            long minutes = java.time.Duration.between(task.getPickedUpAt(), LocalDateTime.now()).toMinutes();
            task.setActualTime((int) minutes);
        }
        task.setActualDistance(task.getEstimatedDistance());
        updateById(task);

        // 恢复骑手在线状态，累加完成订单数
        Rider rider = riderService.getRiderById(riderId);
        rider.setStatus("ONLINE");
        rider.setTotalOrders(rider.getTotalOrders() + 1);
        riderService.updateById(rider);

        log.info("配送任务已完成, taskNo: {}, riderId: {}", taskNo, riderId);
        return task;
    }

    @Override
    @Transactional
    public DeliveryTask cancelTask(Long riderId, String taskNo) {
        DeliveryTask task = getTaskByNo(taskNo);

        if ("COMPLETED".equals(task.getStatus()) || "CANCELLED".equals(task.getStatus())) {
            throw new BusinessException(400, "任务状态不允许取消");
        }
        if (!riderId.equals(task.getRiderId())) {
            throw new BusinessException(403, "无权操作此配送任务");
        }

        task.setStatus("CANCELLED");
        updateById(task);

        // 恢复骑手在线状态
        riderService.updateRiderStatus(riderId, "ONLINE");

        log.info("配送任务已取消, taskNo: {}, riderId: {}", taskNo, riderId);
        return task;
    }

    @Override
    public List<DeliveryTask> listRiderTasks(Long riderId, String status) {
        LambdaQueryWrapper<DeliveryTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryTask::getRiderId, riderId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(DeliveryTask::getStatus, status);
        }
        wrapper.orderByDesc(DeliveryTask::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public DeliveryTask getTaskDetail(String taskNo) {
        return getTaskByNo(taskNo);
    }

    @Override
    public List<DeliveryTask> listAvailableTasks() {
        LambdaQueryWrapper<DeliveryTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryTask::getStatus, "PENDING")
                .orderByDesc(DeliveryTask::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public DeliveryTask getTaskByOrderNo(String orderNo) {
        LambdaQueryWrapper<DeliveryTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryTask::getOrderNo, orderNo);
        return getOne(wrapper);
    }

    /**
     * 根据任务编号获取配送任务
     */
    private DeliveryTask getTaskByNo(String taskNo) {
        LambdaQueryWrapper<DeliveryTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryTask::getTaskNo, taskNo);
        DeliveryTask task = getOne(wrapper);
        if (task == null) {
            throw new BusinessException(404, "配送任务不存在: " + taskNo);
        }
        return task;
    }

    /**
     * 生成配送任务编号
     * 格式：DLV + 时间戳 + 6位随机数
     */
    private String generateTaskNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "DLV" + timestamp + random;
    }

    @Override
    @Transactional
    public DeliveryTask autoDispatchToRider(Long riderId) {
        // 查询待处理的配送任务（商家已备餐完成）
        LambdaQueryWrapper<DeliveryTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryTask::getStatus, "PENDING")
                .orderByAsc(DeliveryTask::getCreatedAt)
                .last("LIMIT 1");
        DeliveryTask task = getOne(wrapper);

        if (task == null) {
            log.info("没有待处理的配送任务, riderId: {}", riderId);
            return null;
        }

        Rider rider = riderService.getRiderById(riderId);

        // 分配骑手
        task.setRiderId(rider.getId());
        task.setRiderName(rider.getName());
        task.setRiderPhone(rider.getPhone());
        task.setStatus("ASSIGNED");
        task.setAssignedAt(LocalDateTime.now());
        updateById(task);

        // 更新骑手状态为忙碌
        riderService.updateRiderStatus(rider.getId(), "BUSY");

        log.info("自动派单成功, taskNo: {}, riderId: {}", task.getTaskNo(), riderId);
        return task;
    }
}
