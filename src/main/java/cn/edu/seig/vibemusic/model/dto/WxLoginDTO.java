package cn.edu.seig.vibemusic.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

// 微信登录DTO
@Data
public class WxLoginDTO implements Serializable {
    @NotBlank(message = "code不能为空")
    private String code;
}
