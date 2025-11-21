package cn.edu.seig.vibemusic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付沙箱配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.sha-xiang")
public class WechatSandboxConfig {
    /**
     * 沙箱APPID
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * API密钥
     */
    private String mchKey;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 是否启用沙箱环境
     */
    private Boolean sandbox;
}