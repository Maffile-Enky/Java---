package com.takeout.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付配置属性
 * 从 application.yml 中读取支付渠道的配置信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    /** 支付宝配置 */
    private Alipay alipay = new Alipay();

    /** 微信支付配置 */
    private Wechat wechat = new Wechat();

    /**
     * 支付宝配置
     */
    @Data
    public static class Alipay {
        /** 应用ID */
        private String appId;
        /** 网关地址 */
        private String gatewayUrl;
        /** 应用私钥 */
        private String privateKey;
        /** 支付宝公钥 */
        private String alipayPublicKey;
        /** 异步通知地址 */
        private String notifyUrl;
        /** 同步跳转地址 */
        private String returnUrl;
    }

    /**
     * 微信支付配置
     */
    @Data
    public static class Wechat {
        /** 应用ID */
        private String appId;
        /** 商户号 */
        private String mchId;
        /** API密钥 */
        private String apiKey;
        /** 异步通知地址 */
        private String notifyUrl;
        /** 网关地址 */
        private String gatewayUrl;
    }
}
