package cn.edu.seig.vibemusic.model.dto;

import cn.edu.seig.vibemusic.constant.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserPhoneRegisterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     * 手机号格式：1开头，11位数字
     */
    @NotBlank(message = MessageConstant.PHONE + MessageConstant.NOT_NULL)
    @Pattern(regexp = "^1[3456789]\\d{9}$", message = MessageConstant.PHONE + MessageConstant.FORMAT_ERROR)
    private String phone;

    /**
     * 用户密码
     * 密码格式：8-18 位数字、字母、符号的任意两种组合
     */
    @NotBlank(message = MessageConstant.PASSWORD + MessageConstant.NOT_NULL)
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{8,18}$", message = MessageConstant.PASSWORD + MessageConstant.FORMAT_ERROR)
    private String password;

    /**
     * 验证码
     * 验证码格式：6位数字
     */
    @NotBlank(message = MessageConstant.VERIFICATION_CODE + MessageConstant.NOT_NULL)
    @Pattern(regexp = "^[0-9]{6}$", message = MessageConstant.VERIFICATION_CODE + MessageConstant.FORMAT_ERROR)
    private String verificationCode;
}