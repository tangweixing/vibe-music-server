package cn.edu.seig.vibemusic.service.impl;

import cn.edu.seig.vibemusic.constant.MessageConstant;
import cn.edu.seig.vibemusic.service.EmailService;
import cn.edu.seig.vibemusic.template.EmailTemplate;
import cn.edu.seig.vibemusic.util.RandomCodeUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sunpingli
 * @since 2025-01-09
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件
     *
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 发送结果，包含是否成功
     */
    public boolean sendEmail(String to, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            log.error(MessageConstant.EMAIL_SEND_FAILED, e);
            return false;
        }
    }

    /**
     * 发送验证码邮件
     *
     * @param email 收件人地址
     * @return 发送结果，包含是否成功和验证码
     */
    public String sendVerificationCodeEmail(String email) {
        String verificationCode = RandomCodeUtil.generateRandomCode();
        String subject = "【Vibe Music】验证码";
        String content = "您的验证码为：" + verificationCode;
        boolean success = sendEmail(email, subject, content);
        if (success) {
            return verificationCode;
        } else {
            return null;
        }
    }

    // 实现发送推广邮件方法
    @Override
    public boolean sendPromotionEmail(String to, String username, String content, String url) {
        String subject = "【Vibe Music】专属福利活动";
        String htmlContent = EmailTemplate.getPromotionTemplate(username, content, url);
        return sendHtmlEmail(to, subject, htmlContent);
    }

    // 新增发送HTML邮件方法
    private boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // 第二个参数设为true表示发送HTML内容
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            log.error("推广邮件发送失败", e);
            return false;
        }
    }
}
