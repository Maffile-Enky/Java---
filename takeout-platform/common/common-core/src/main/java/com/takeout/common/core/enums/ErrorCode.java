package com.takeout.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USER_PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "账户已被禁用"),
    USER_PHONE_INVALID(1005, "手机号格式不正确"),
    USER_SMS_CODE_ERROR(1006, "验证码错误或已过期"),
    USER_WECHAT_AUTH_FAILED(1007, "微信授权失败"),

    // 商家相关 2xxx
    MERCHANT_NOT_FOUND(2001, "商家不存在"),
    MERCHANT_DISABLED(2002, "商家已停业"),

    // 订单相关 3xxx
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态异常"),

    // 签名相关 4xxx
    SIGN_INVALID(4001, "签名验证失败"),
    SIGN_EXPIRED(4002, "签名已过期"),
    SIGN_REPLAY(4003, "重放攻击检测"),

    // 限流相关 5xxx
    RATE_LIMIT_EXCEEDED(429, "请求过于频繁，请稍后再试");

    private final int code;
    private final String message;
}
