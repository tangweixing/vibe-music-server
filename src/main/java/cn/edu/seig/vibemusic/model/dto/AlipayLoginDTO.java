package cn.edu.seig.vibemusic.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

// 支付宝登录DTO
@Data
public class AlipayLoginDTO implements Serializable {
    @NotBlank(message = "authCode不能为空")
    private String authCode;
}