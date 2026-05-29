package com.takeout.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.common.redis.service.CacheService;
import com.takeout.payment.config.PaymentProperties;
import com.takeout.payment.config.RabbitMQConfig;
import com.takeout.payment.dto.PaymentCreateDTO;
import com.takeout.payment.dto.PaymentResultDTO;
import com.takeout.payment.entity.PaymentOrder;
import com.takeout.payment.event.PaymentSuccessEvent;
import com.takeout.payment.mapper.PaymentOrderMapper;
import com.takeout.payment.service.PaymentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 支付流水服务实现类
 * 核心职责：创建支付订单、处理回调、退款、状态管理
 * 防重复：Redisson 分布式锁 + 乐观锁版本号
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder>
        implements PaymentOrderService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RedissonClient redissonClient;
    private final CacheService cacheService;
    private final PaymentProperties paymentProperties;

    /** Redis 分布式锁前缀 */
    private static final String LOCK_PAYMENT_PREFIX = "lock:payment:";

    /** Redis 幂等键前缀（防重复回调） */
    private static final String IDEMPOTENT_PAYMENT_PREFIX = "idempotent:payment:callback:";

    /** 支付超时时间（分钟） */
    private static final int PAYMENT_EXPIRE_MINUTES = 30;

    /**
     * 创建支付订单（统一下单）
     * 流程：参数校验 -> 幂等检查 -> 生成流水号 -> 调用第三方 -> 返回支付信息
     */
    @Override
    @Transactional
    public PaymentResultDTO createPayment(Long userId, PaymentCreateDTO dto) {
        log.info("创建支付订单, userId: {}, orderNo: {}, channel: {}", userId, dto.getOrderNo(), dto.getPayChannel());

        // 参数校验
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "支付金额必须大于0");
        }

        // 分布式锁防重复下单
        String lockKey = LOCK_PAYMENT_PREFIX + dto.getOrderNo();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 尝试获取锁，等待5秒，持有30秒
            boolean acquired = lock.tryLock(5, 30, TimeUnit.SECONDS);
            if (!acquired) {
                throw new BusinessException(429, "请勿重复提交支付");
            }

            // 检查该订单是否已有进行中的支付
            LambdaQueryWrapper<PaymentOrder> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(PaymentOrder::getOrderNo, dto.getOrderNo())
                    .in(PaymentOrder::getStatus, "CREATED", "PENDING", "SUCCESS");
            PaymentOrder existing = paymentOrderMapper.selectOne(checkWrapper);
            if (existing != null && "SUCCESS".equals(existing.getStatus())) {
                throw new BusinessException(400, "该订单已支付成功");
            }
            if (existing != null && ("CREATED".equals(existing.getStatus()) || "PENDING".equals(existing.getStatus()))) {
                // 返回已有待支付的流水信息
                return buildPaymentResult(existing);
            }

            // 生成支付流水
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setPaymentNo(generatePaymentNo());
            paymentOrder.setOrderNo(dto.getOrderNo());
            paymentOrder.setUserId(userId);
            paymentOrder.setAmount(dto.getAmount());
            paymentOrder.setPayChannel(dto.getPayChannel());
            paymentOrder.setPayType(dto.getPayType());
            paymentOrder.setStatus("PENDING");
            paymentOrder.setExpireTime(LocalDateTime.now().plusMinutes(PAYMENT_EXPIRE_MINUTES));
            paymentOrder.setRemark(dto.getSubject());
            paymentOrderMapper.insert(paymentOrder);

            log.info("支付订单创建成功, paymentNo: {}", paymentOrder.getPaymentNo());

            // 调用第三方支付统一下单（沙箱环境模拟）
            String payInfo = simulateThirdPartyPay(paymentOrder);

            // 构建返回结果
            PaymentResultDTO result = new PaymentResultDTO();
            result.setPaymentNo(paymentOrder.getPaymentNo());
            result.setStatus("PENDING");
            result.setPayInfo(payInfo);
            result.setExpireSeconds(PAYMENT_EXPIRE_MINUTES * 60L);
            return result;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 处理支付宝异步回调
     * 流程：验签 -> 幂等检查 -> 更新状态 -> 发送MQ事件
     */
    @Override
    @Transactional
    public boolean handleAlipayCallback(Map<String, String> callbackParams) {
        String tradeNo = callbackParams.get("trade_no");
        String outTradeNo = callbackParams.get("out_trade_no");
        String tradeStatus = callbackParams.get("trade_status");

        log.info("收到支付宝回调, tradeNo: {}, outTradeNo: {}, tradeStatus: {}", tradeNo, outTradeNo, tradeStatus);

        // 防重复回调（幂等性保证）
        String idempotentKey = IDEMPOTENT_PAYMENT_PREFIX + "alipay:" + tradeNo;
        if (Boolean.TRUE.equals(cacheService.hasKey(idempotentKey))) {
            log.warn("支付宝回调重复, tradeNo: {}, 已处理过", tradeNo);
            return true;
        }

        // 验签逻辑（沙箱环境预留）
        boolean signVerified = verifyAlipaySign(callbackParams);
        if (!signVerified) {
            log.error("支付宝回调验签失败, outTradeNo: {}", outTradeNo);
            throw new BusinessException(500, "验签失败");
        }

        // 查询支付流水
        PaymentOrder paymentOrder = getByPaymentNo(outTradeNo);
        if (paymentOrder == null) {
            log.error("支付流水不存在, paymentNo: {}", outTradeNo);
            throw new BusinessException(404, "支付流水不存在");
        }

        // 处理支付成功
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            processPaymentSuccess(paymentOrder, tradeNo, callbackParams.toString());
        }

        // 标记已处理（设置24小时过期，防止重复回调）
        cacheService.set(idempotentKey, "1", 24, TimeUnit.HOURS);
        return true;
    }

    /**
     * 处理微信支付异步回调
     * 流程：验签 -> 幂等检查 -> 更新状态 -> 发送MQ事件
     */
    @Override
    @Transactional
    public boolean handleWechatCallback(String callbackBody) {
        log.info("收到微信支付回调, bodyLength: {}", callbackBody.length());

        // 解析回调数据（简化处理，实际应解析XML或JSON）
        String tradeNo = extractValue(callbackBody, "transaction_id");
        String outTradeNo = extractValue(callbackBody, "out_trade_no");
        String tradeState = extractValue(callbackBody, "trade_state");

        // 防重复回调（幂等性保证）
        String idempotentKey = IDEMPOTENT_PAYMENT_PREFIX + "wechat:" + tradeNo;
        if (Boolean.TRUE.equals(cacheService.hasKey(idempotentKey))) {
            log.warn("微信支付回调重复, tradeNo: {}", tradeNo);
            return true;
        }

        // 验签逻辑（沙箱环境预留）
        boolean signVerified = verifyWechatSign(callbackBody);
        if (!signVerified) {
            log.error("微信支付回调验签失败, outTradeNo: {}", outTradeNo);
            throw new BusinessException(500, "验签失败");
        }

        // 查询支付流水
        PaymentOrder paymentOrder = getByPaymentNo(outTradeNo);
        if (paymentOrder == null) {
            log.error("支付流水不存在, paymentNo: {}", outTradeNo);
            throw new BusinessException(404, "支付流水不存在");
        }

        // 处理支付成功
        if ("SUCCESS".equals(tradeState)) {
            processPaymentSuccess(paymentOrder, tradeNo, callbackBody);
        }

        // 标记已处理
        cacheService.set(idempotentKey, "1", 24, TimeUnit.HOURS);
        return true;
    }

    /**
     * 查询支付状态
     */
    @Override
    public PaymentOrder queryPaymentStatus(String paymentNo) {
        PaymentOrder paymentOrder = getByPaymentNo(paymentNo);
        if (paymentOrder == null) {
            throw new BusinessException(404, "支付流水不存在");
        }
        return paymentOrder;
    }

    /**
     * 查询用户的支付记录
     */
    @Override
    public IPage<PaymentOrder> listUserPayments(Long userId, Page<PaymentOrder> page) {
        LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentOrder::getUserId, userId)
                .orderByDesc(PaymentOrder::getCreatedAt);
        return paymentOrderMapper.selectPage(page, wrapper);
    }

    /**
     * 申请退款
     * 流程：状态校验 -> 更新状态为已退款 -> 发送退款MQ事件
     */
    @Override
    @Transactional
    public boolean refund(Long userId, String paymentNo, String reason) {
        log.info("申请退款, userId: {}, paymentNo: {}, reason: {}", userId, paymentNo, reason);

        PaymentOrder paymentOrder = getByPaymentNo(paymentNo);
        if (paymentOrder == null) {
            throw new BusinessException(404, "支付流水不存在");
        }
        if (!paymentOrder.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该支付流水");
        }
        if (!"SUCCESS".equals(paymentOrder.getStatus())) {
            throw new BusinessException(400, "只有支付成功的订单才能退款");
        }

        // 更新状态为已退款
        paymentOrder.setStatus("REFUNDED");
        paymentOrder.setRefundReason(reason);
        paymentOrder.setRemark("退款时间: " + LocalDateTime.now());
        int rows = paymentOrderMapper.updateById(paymentOrder);
        if (rows <= 0) {
            throw new BusinessException(500, "退款失败，请重试");
        }

        log.info("退款成功, paymentNo: {}", paymentNo);
        return true;
    }

    /**
     * 关闭未支付的支付订单
     */
    @Override
    @Transactional
    public boolean closePayment(String paymentNo) {
        log.info("关闭支付订单, paymentNo: {}", paymentNo);

        PaymentOrder paymentOrder = getByPaymentNo(paymentNo);
        if (paymentOrder == null) {
            throw new BusinessException(404, "支付流水不存在");
        }
        if (!"PENDING".equals(paymentOrder.getStatus())) {
            throw new BusinessException(400, "当前状态不可关闭");
        }

        paymentOrder.setStatus("CLOSED");
        int rows = paymentOrderMapper.updateById(paymentOrder);
        if (rows <= 0) {
            throw new BusinessException(500, "关闭失败，请重试");
        }

        log.info("支付订单关闭成功, paymentNo: {}", paymentNo);
        return true;
    }

    /**
     * 模拟支付成功（沙箱环境专用）
     * 前端倒计时结束后调用，模拟用户完成支付
     */
    @Override
    @Transactional
    public boolean mockPaymentSuccess(Long userId, String paymentNo) {
        log.info("模拟支付成功, userId: {}, paymentNo: {}", userId, paymentNo);

        PaymentOrder paymentOrder = getByPaymentNo(paymentNo);
        if (paymentOrder == null) {
            throw new BusinessException(404, "支付流水不存在");
        }
        if (!paymentOrder.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该支付流水");
        }
        if (!"PENDING".equals(paymentOrder.getStatus())) {
            throw new BusinessException(400, "当前状态不可模拟支付");
        }

        // 模拟第三方交易号
        String mockTradeNo = "MOCK_" + System.currentTimeMillis();
        // 复用支付成功处理逻辑
        processPaymentSuccess(paymentOrder, mockTradeNo, "mock_callback");

        log.info("模拟支付成功完成, paymentNo: {}", paymentNo);
        return true;
    }

    // ==================== 私有方法 ====================

    /**
     * 处理支付成功的核心逻辑
     * 1. 更新支付流水状态
     * 2. 通过 RabbitMQ 发送支付成功事件（通知订单服务 + 通知服务）
     */
    private void processPaymentSuccess(PaymentOrder paymentOrder, String tradeNo, String callbackData) {
        // 乐观锁更新状态
        paymentOrder.setStatus("SUCCESS");
        paymentOrder.setTradeNo(tradeNo);
        paymentOrder.setPayTime(LocalDateTime.now());
        paymentOrder.setCallbackData(callbackData);

        int rows = paymentOrderMapper.updateById(paymentOrder);
        if (rows <= 0) {
            log.error("更新支付状态失败，可能存在并发更新, paymentNo: {}", paymentOrder.getPaymentNo());
            throw new BusinessException(500, "支付状态更新失败");
        }

        // 构建支付成功事件
        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .paymentNo(paymentOrder.getPaymentNo())
                .orderNo(paymentOrder.getOrderNo())
                .userId(paymentOrder.getUserId())
                .amount(paymentOrder.getAmount())
                .payChannel(paymentOrder.getPayChannel())
                .tradeNo(tradeNo)
                .timestamp(System.currentTimeMillis())
                .build();

        // 发送 MQ 事件到订单服务和通知服务（Fanout 路由到两个队列）
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_SUCCESS_ROUTING_KEY,
                event
        );

        log.info("支付成功事件已发送, paymentNo: {}, orderNo: {}", paymentOrder.getPaymentNo(), paymentOrder.getOrderNo());
    }

    /**
     * 生成支付流水号
     * 格式：PAY + 时间戳 + 6位随机数
     */
    private String generatePaymentNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "PAY" + timestamp + random;
    }

    /**
     * 根据支付流水号查询
     */
    private PaymentOrder getByPaymentNo(String paymentNo) {
        LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentOrder::getPaymentNo, paymentNo);
        return paymentOrderMapper.selectOne(wrapper);
    }

    /**
     * 构建支付结果响应
     */
    private PaymentResultDTO buildPaymentResult(PaymentOrder paymentOrder) {
        PaymentResultDTO result = new PaymentResultDTO();
        result.setPaymentNo(paymentOrder.getPaymentNo());
        result.setStatus(paymentOrder.getStatus());
        if (paymentOrder.getExpireTime() != null) {
            result.setExpireSeconds(
                    java.time.Duration.between(LocalDateTime.now(), paymentOrder.getExpireTime()).getSeconds()
            );
        }
        return result;
    }

    /**
     * 模拟调用第三方支付统一下单（沙箱环境）
     * 实际生产环境对接支付宝/微信 SDK
     */
    private String simulateThirdPartyPay(PaymentOrder paymentOrder) {
        String channel = paymentOrder.getPayChannel();
        log.info("调用第三方支付统一下单（沙箱模拟）, channel: {}, amount: {}", channel, paymentOrder.getAmount());

        // 根据不同支付渠道返回不同的支付信息
        if ("ALIPAY".equals(channel)) {
            // 支付宝：返回二维码链接
            return "https://qr.alipay.com/sandbox_" + UUID.randomUUID().toString().substring(0, 8);
        } else if ("WECHAT".equals(channel)) {
            // 微信支付：返回预支付ID
            return "wx" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000, 9999);
        }

        throw new BusinessException(400, "不支持的支付渠道: " + channel);
    }

    /**
     * 支付宝回调验签逻辑（预留）
     * 生产环境使用支付宝 SDK 的 AlipaySignature.rsaCheckV1 方法
     */
    private boolean verifyAlipaySign(Map<String, String> callbackParams) {
        // TODO: 生产环境实现支付宝签名验证
        // AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2")
        log.info("支付宝验签（沙箱环境跳过验证）");
        return true;
    }

    /**
     * 微信支付回调验签逻辑（预留）
     * 生产环境使用微信支付 SDK 的签名验证
     */
    private boolean verifyWechatSign(String callbackBody) {
        // TODO: 生产环境实现微信支付签名验证
        // 使用微信支付API v3的平台证书验证签名
        log.info("微信支付验签（沙箱环境跳过验证）");
        return true;
    }

    /**
     * 从XML/JSON字符串中提取指定字段值（简化工具方法）
     */
    private String extractValue(String body, String key) {
        // 简化实现，实际应使用XML/JSON解析
        String searchKey = "<" + key + ">";
        String endKey = "</" + key + ">";
        int start = body.indexOf(searchKey);
        int end = body.indexOf(endKey);
        if (start >= 0 && end > start) {
            return body.substring(start + searchKey.length(), end);
        }
        return "";
    }
}
