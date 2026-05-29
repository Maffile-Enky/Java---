package com.takeout.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.delivery.dto.DeliveryTaskCreateDTO;
import com.takeout.delivery.dto.DispatchResultDTO;
import com.takeout.delivery.entity.DeliveryTask;

import java.util.List;

/**
 * 配送任务服务接口
 */
public interface DeliveryTaskService extends IService<DeliveryTask> {

    /**
     * 创建配送任务（订单支付成功后调用）
     *
     * @param dto 配送任务创建信息
     * @return 创建的配送任务
     */
    DeliveryTask createTask(DeliveryTaskCreateDTO dto);

    /**
     * 智能调度分配骑手
     *
     * @param taskNo 配送任务编号
     * @return 调度结果
     */
    DispatchResultDTO dispatchOrder(String taskNo);

    /**
     * 骑手取餐
     *
     * @param riderId 骑手ID
     * @param taskNo  配送任务编号
     * @return 更新后的配送任务
     */
    DeliveryTask pickUpOrder(Long riderId, String taskNo);

    /**
     * 骑手送达
     *
     * @param riderId 骑手ID
     * @param taskNo  配送任务编号
     * @return 更新后的配送任务
     */
    DeliveryTask deliverOrder(Long riderId, String taskNo);

    /**
     * 取消配送任务
     *
     * @param riderId 骑手ID
     * @param taskNo  配送任务编号
     * @return 更新后的配送任务
     */
    DeliveryTask cancelTask(Long riderId, String taskNo);

    /**
     * 查询骑手的配送任务列表
     *
     * @param riderId 骑手ID
     * @param status  任务状态（可选）
     * @return 配送任务列表
     */
    List<DeliveryTask> listRiderTasks(Long riderId, String status);

    /**
     * 获取配送任务详情
     *
     * @param taskNo 配送任务编号
     * @return 配送任务详情
     */
    DeliveryTask getTaskDetail(String taskNo);

    /**
     * 查询可抢单的配送任务列表
     *
     * @return 待分配的配送任务列表
     */
    List<DeliveryTask> listAvailableTasks();

    /**
     * 根据订单号获取配送任务
     *
     * @param orderNo 订单号
     * @return 配送任务
     */
    DeliveryTask getTaskByOrderNo(String orderNo);

    /**
     * 自动派单给指定骑手
     * 查找待处理的配送任务并分配给骑手
     *
     * @param riderId 骑手ID
     * @return 分配的任务，如果没有待处理任务则返回null
     */
    DeliveryTask autoDispatchToRider(Long riderId);
}
