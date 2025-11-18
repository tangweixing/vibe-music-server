package cn.edu.seig.vibemusic.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

@Data
public class PromotionEmailDTO implements Serializable {
    @NotBlank(message = "收件人邮箱不能为空")
    @Email(message = "邮箱格式错误")
    private String email;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "邮件内容不能为空")
    private String content;
    
    @URL(message = "链接格式错误")
    private String url;
}