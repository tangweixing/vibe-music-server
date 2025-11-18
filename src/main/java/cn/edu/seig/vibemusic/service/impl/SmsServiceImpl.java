package cn.edu.seig.vibemusic.service.impl;

import cn.edu.seig.vibemusic.constant.MessageConstant;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.SmsService;
import cn.edu.seig.vibemusic.util.RandomCodeUtil;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import darabonba.core.exception.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    // 阿里云短信配置（个人开发者需自行申请）
    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.signName}")
    private String signName;

    @Value("${aliyun.sms.templateCode}")
    private String templateCode;

    /**
     * 发送手机验证码
     * @param phone 手机号
     * @return 验证码（发送成功返回，失败返回null）
     */
    @Override
    public String sendVerificationCode(String phone) {
        // 生成6位自定义验证码
        String verificationCode = RandomCodeUtil.generateRandomNumberCode();
        try {
            // 构建凭证提供者（使用配置的AccessKey）
            StaticCredentialProvider credentialProvider = StaticCredentialProvider.create(
                    Credential.builder()
                            .accessKeyId(accessKeyId)
                            .accessKeySecret(accessKeySecret)
                            .build()
            );

            // 初始化阿里云异步客户端（配置地域和Endpoint）
            AsyncClient client = AsyncClient.builder()
                    .region("ap-southeast-1")
                    .credentialsProvider(credentialProvider)
                    .overrideConfiguration(
                            ClientOverrideConfiguration.create()
                                    .setEndpointOverride("dypnsapi.aliyuncs.com")
                    )
                    .build();

            // 组装短信请求参数（使用自定义生成的验证码）
            SendSmsVerifyCodeRequest request = SendSmsVerifyCodeRequest.builder()
                    .signName(signName) // 从配置读取签名
                    .templateCode(templateCode) // 从配置读取模板CODE
                    .phoneNumber(phone) // 传入目标手机号
                    .templateParam("{\"code\":\"" + verificationCode + "\",\"min\":\"5\"}") // 替换为自定义验证码
                    .build();

            // 异步发送短信并获取响应
            CompletableFuture<SendSmsVerifyCodeResponse> future = client.sendSmsVerifyCode(request);
            SendSmsVerifyCodeResponse response = future.get(); // 同步等待结果

            // 处理发送结果
            if ("OK".equals(response.getBody().getMessage())) {
                log.info("短信发送成功，手机号：{}，验证码：{}", phone, verificationCode);
                return verificationCode;
            } else {
                log.error("短信发送失败，错误码：{}，错误信息：{}", response.getStatusCode(), response.getBody().getMessage());
                return MessageConstant.SMS_SEND_FAILED;
            }
        } catch (Exception e) {
            // 捕获所有异常（包括网络、SDK、业务逻辑异常）
            log.error("发送短信时发生异常，手机号：{}，异常信息：{}", phone, e.getMessage(), e);
            return MessageConstant.SMS_SEND_FAILED;
        }
    }
}