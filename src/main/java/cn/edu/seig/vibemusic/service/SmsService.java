package cn.edu.seig.vibemusic.service;

/**
 * 短信服务接口
 */
public interface SmsService {

    // 发送手机验证码
    String sendVerificationCode(String phone);
}