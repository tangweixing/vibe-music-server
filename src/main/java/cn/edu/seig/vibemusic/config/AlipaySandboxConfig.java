package cn.edu.seig.vibemusic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝沙箱配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay.sha-xiang")
public class AlipaySandboxConfig {
    /**
     * 沙箱APPID
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String privateKey;

    /**
     * 支付宝公钥
     */
    private String publicKey;

    /**
     * 沙箱网关
     */
    private String serverUrl;

    /**
     * 异步回调地址
     */
    private String notifyUrl;

    /**
     * 同步跳转地址
     */
    private String returnUrl;
}