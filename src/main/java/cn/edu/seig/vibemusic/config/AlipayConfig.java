package cn.edu.seig.vibemusic.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient; // 关键导入
// 支付宝登录配置
@Configuration
@Getter
public class AlipayConfig {
    @Value("${alipay.app-id}")
    private String appId;
    @Value("${alipay.private-key}")
    private String privateKey;
    @Value("${alipay.public-key}")
    private String publicKey;
    @Value("${alipay.gateway-url}")
    private String gatewayUrl;
    @Value("${alipay.redirect-url}")
    private String redirectUrl;

    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
            gatewayUrl, appId, privateKey,
            "json", "UTF-8", publicKey, "RSA2"
        );
    }



}