// 充值请求DTO
package cn.edu.seig.vibemusic.model.dto;

import cn.edu.seig.vibemusic.constant.MessageConstant;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = MessageConstant.AMOUNT + MessageConstant.NOT_NULL)
    @DecimalMin(value = "0.01", message = MessageConstant.AMOUNT + MessageConstant.MIN_ERROR)
    private BigDecimal amount;

    @NotNull(message = MessageConstant.PAY_TYPE + MessageConstant.NOT_NULL)
    private Integer payType; // 1-微信，2-支付宝

    @NotBlank(message = MessageConstant.CALLBACK_URL + MessageConstant.NOT_NULL)
    private String returnUrl;
}
