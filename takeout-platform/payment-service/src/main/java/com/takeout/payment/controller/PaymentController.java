package com.takeout.payment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.common.core.domain.Result;
import com.takeout.common.web.util.SecurityUtil;
import com.takeout.payment.dto.PaymentCreateDTO;
import com.takeout.payment.dto.PaymentResultDTO;
import com.takeout.payment.entity.PaymentOrder;
import com.takeout.payment.service.PaymentOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付服务控制器
 * 提供：统一下单、回调处理、查询、退款等接口
 */
@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentOrderService paymentOrderService;

    /**
     * 统一下单接口
     * POST /payment/create
     * 前端选择支付方式后调用，返回支付二维码/跳转链接
     */
    @PostMapping("/create")
    public Result<PaymentResultDTO> createPayment(HttpServletRequest request,
                                                   @RequestBody PaymentCreateDTO dto) {
        Long userId = SecurityUtil.getUserId(request);
        log.info("用户发起支付, userId: {}, orderNo: {}", userId, dto.getOrderNo());
        PaymentResultDTO result = paymentOrderService.createPayment(userId, dto);
        return Result.success(result);
    }

    /**
     * 支付宝异步回调接口
     * POST /payment/callback/alipay
     * 支付宝在支付完成后会主动调用此接口通知支付结果
     */
    @PostMapping("/callback/alipay")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        log.info("收到支付宝回调, params: {}", params);
        try {
            boolean success = paymentOrderService.handleAlipayCallback(params);
            return success ? "success" : "failure";
        } catch (Exception e) {
            log.error("处理支付宝回调异常", e);
            return "failure";
        }
    }

    /**
     * 微信支付异步回调接口
     * POST /payment/callback/wechat
     * 微信支付在支付完成后会主动调用此接口通知支付结果
     */
    @PostMapping("/callback/wechat")
    public String wechatCallback(@RequestBody String body) {
        log.info("收到微信支付回调");
        try {
            boolean success = paymentOrderService.handleWechatCallback(body);
            return success ? "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>"
                    : "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        } catch (Exception e) {
            log.error("处理微信支付回调异常", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理异常]]></return_msg></xml>";
        }
    }

    /**
     * 查询支付状态
     * GET /payment/status/{paymentNo}
     * 前端轮询支付结果时调用
     */
    @GetMapping("/status/{paymentNo}")
    public Result<PaymentOrder> queryPaymentStatus(HttpServletRequest request, @PathVariable String paymentNo) {
        Long userId = SecurityUtil.getUserId(request);
        PaymentOrder paymentOrder = paymentOrderService.queryPaymentStatus(paymentNo);
        if (paymentOrder == null || !paymentOrder.getUserId().equals(userId)) {
            return Result.error(404, "支付订单不存在");
        }
        return Result.success(paymentOrder);
    }

    /**
     * 查询用户的支付记录
     * GET /payment/list
     */
    @GetMapping("/list")
    public Result<IPage<PaymentOrder>> listPayments(HttpServletRequest request,
                                                      @RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtil.getUserId(request);
        IPage<PaymentOrder> result = paymentOrderService.listUserPayments(userId, new Page<>(page, size));
        return Result.success(result);
    }

    /**
     * 申请退款
     * POST /payment/refund
     */
    @PostMapping("/refund")
    public Result<Boolean> refund(HttpServletRequest request,
                                  @RequestParam String paymentNo,
                                  @RequestParam(required = false, defaultValue = "用户申请退款") String reason) {
        Long userId = SecurityUtil.getUserId(request);
        log.info("用户申请退款, userId: {}, paymentNo: {}", userId, paymentNo);
        boolean success = paymentOrderService.refund(userId, paymentNo, reason);
        return Result.success(success);
    }

    /**
     * 关闭支付订单
     * POST /payment/close
     */
    @PostMapping("/close")
    public Result<Boolean> closePayment(HttpServletRequest request, @RequestParam String paymentNo) {
        Long userId = SecurityUtil.getUserId(request);
        log.info("关闭支付订单, userId: {}, paymentNo: {}", userId, paymentNo);
        boolean success = paymentOrderService.closePayment(paymentNo);
        return Result.success(success);
    }

    /**
     * 模拟支付成功（沙箱环境专用）
     * POST /payment/mock-success
     * 前端倒计时结束后调用，模拟用户完成支付
     */
    @PostMapping("/mock-success")
    public Result<Boolean> mockPaymentSuccess(HttpServletRequest request, @RequestParam String paymentNo) {
        Long userId = SecurityUtil.getUserId(request);
        log.info("模拟支付成功, userId: {}, paymentNo: {}", userId, paymentNo);
        boolean success = paymentOrderService.mockPaymentSuccess(userId, paymentNo);
        return Result.success(success);
    }
}
