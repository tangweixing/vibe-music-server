package cn.edu.seig.vibemusic.enumeration;

import lombok.Getter;

// 提现方式枚举（新增）
@Getter
public enum WithdrawTypeEnum {
    WECHAT(1, "微信提现"),
    ALIPAY(2, "支付宝提现");

    private final Integer type;
    private final String desc;

    WithdrawTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}