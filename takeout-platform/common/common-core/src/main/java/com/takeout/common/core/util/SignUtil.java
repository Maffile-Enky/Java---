package com.takeout.common.core.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * API signature verification utility.
 * Supports HMAC-SHA256 signing to prevent tampering and replay attacks.
 *
 * Signature algorithm:
 * 1. Sort all request params by key
 * 2. Concatenate as key1=value1&key2=value2
 * 3. Append timestamp and nonce
 * 4. HMAC-SHA256 with shared secret
 */
public class SignUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final long SIGN_EXPIRE_MS = 5 * 60 * 1000L; // 5 minutes

    /**
     * Generate signature for the given params.
     */
    public static String sign(Map<String, String> params, String secret) {
        try {
            // Sort by key
            TreeMap<String, String> sorted = new TreeMap<>(params);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : sorted.entrySet()) {
                if (sb.length() > 0) sb.append("&");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
            String toSign = sb.toString();

            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(keySpec);
            byte[] hash = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));

            // Hex encode
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(b & 0xff);
                if (h.length() == 1) hex.append("0");
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("签名生成失败", e);
        }
    }

    /**
     * Verify signature and check timestamp freshness.
     *
     * @param params   request params (excluding 'sign' itself)
     * @param sign     the signature to verify
     * @param secret   shared secret
     * @param timestamp request timestamp (ms)
     * @param nonce    unique nonce to prevent replay
     * @return true if valid
     */
    public static boolean verify(Map<String, String> params, String sign, String secret,
                                 long timestamp, String nonce) {
        // Check timestamp freshness
        if (Math.abs(System.currentTimeMillis() - timestamp) > SIGN_EXPIRE_MS) {
            return false;
        }
        // Add timestamp and nonce to params for signing
        Map<String, String> signParams = new HashMap<>(params);
        signParams.put("timestamp", String.valueOf(timestamp));
        signParams.put("nonce", nonce);

        String expected = sign(signParams, secret);
        return expected.equalsIgnoreCase(sign);
    }
}
