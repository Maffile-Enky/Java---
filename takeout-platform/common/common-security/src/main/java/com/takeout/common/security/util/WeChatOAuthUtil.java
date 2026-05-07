package com.takeout.common.security.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * WeChat OAuth2 utility.
 * Handles WeChat OAuth2 authorization code flow:
 * 1. Frontend gets auth code via WeChat JS SDK
 * 2. Backend exchanges code for access_token + openid
 * 3. Backend fetches user info from WeChat API
 */
@Slf4j
public class WeChatOAuthUtil {

    private static final String TOKEN_URL =
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private static final String USER_INFO_URL =
            "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Exchange authorization code for access token and openid.
     *
     * @return WeChatTokenResult containing accessToken and openid, or null on failure
     */
    public static WeChatTokenResult getAccessToken(String appId, String appSecret, String code) {
        try {
            String url = String.format(TOKEN_URL, appId, appSecret, code);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = MAPPER.readTree(response.body());

            if (node.has("errcode") && node.get("errcode").asInt() != 0) {
                log.error("微信获取access_token失败: {}", node.get("errmsg").asText());
                return null;
            }

            WeChatTokenResult result = new WeChatTokenResult();
            result.setAccessToken(node.get("access_token").asText());
            result.setOpenid(node.get("openid").asText());
            if (node.has("refresh_token")) {
                result.setRefreshToken(node.get("refresh_token").asText());
            }
            return result;
        } catch (Exception e) {
            log.error("微信OAuth异常", e);
            return null;
        }
    }

    /**
     * Fetch WeChat user info using access token.
     */
    public static WeChatUserInfo getUserInfo(String accessToken, String openid) {
        try {
            String url = String.format(USER_INFO_URL, accessToken, openid);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = MAPPER.readTree(response.body());

            if (node.has("errcode") && node.get("errcode").asInt() != 0) {
                log.error("微信获取用户信息失败: {}", node.get("errmsg").asText());
                return null;
            }

            WeChatUserInfo info = new WeChatUserInfo();
            info.setOpenid(node.get("openid").asText());
            info.setNickname(node.has("nickname") ? node.get("nickname").asText() : "");
            info.setHeadImgUrl(node.has("headimgurl") ? node.get("headimgurl").asText() : "");
            info.setSex(node.has("sex") ? node.get("sex").asInt() : 0);
            return info;
        } catch (Exception e) {
            log.error("微信获取用户信息异常", e);
            return null;
        }
    }

    @lombok.Data
    public static class WeChatTokenResult {
        private String accessToken;
        private String openid;
        private String refreshToken;
    }

    @lombok.Data
    public static class WeChatUserInfo {
        private String openid;
        private String nickname;
        private String headImgUrl;
        private int sex;
    }
}
