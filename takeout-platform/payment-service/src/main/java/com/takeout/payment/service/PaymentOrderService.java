package com.takeout.payment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.payment.dto.PaymentCreateDTO;
import com.takeout.payment.dto.PaymentResultDTO;
import com.takeout.payment.entity.PaymentOrder;

import java.util.Map;

/**
 * 支付流水服务接口
 */
public interface PaymentOrderService extends IService<PaymentOrder> {

    /**
     * 创建支付订单（统一下单）
     * @param userId 用户ID
     * @param dto 支付请求参数
     * @return 支付结果（含二维码/跳转链接等）
     */
    PaymentResultDTO createPayment(Long userId, PaymentCreateDTO dto);

    /**
     * 处理支付宝异步回调
     * @param callbackParams 回调参数
     * @return 处理结果
     */
    boolean handleAlipayCallback(Map<String, String> callbackParams);

    /**
     * 处理微信支付异步回调
     * @param callbackBody 回调请求体（XML）
     * @return 处理结果
     */
    boolean handleWechatCallback(String callbackBody);

    /**
     * 查询支付状态
     * @param paymentNo 支付流水号
     * @return 支付流水
     */
    PaymentOrder queryPaymentStatus(String paymentNo);

    /**
     * 查询用户的支付记录
     * @param userId 用户ID
     * @param page 分页参数
     * @return 支付记录分页
     */
    IPage<PaymentOrder> listUserPayments(Long userId, Page<PaymentOrder> page);

    /**
     * 申请退款
     * @param userId 用户ID
     * @param paymentNo 支付流水号
     * @param reason 退款原因
     * @return 退款结果
     */
    boolean refund(Long userId, String paymentNo, String reason);

    /**
     * 关闭未支付的支付订单
     * @param paymentNo 支付流水号
     * @return 是否成功
     */
    boolean closePayment(String paymentNo);
}
