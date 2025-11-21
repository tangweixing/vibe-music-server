package cn.edu.seig.vibemusic.enumeration;

import lombok.Getter;

// 充值状态枚举
@Getter
public enum RechargeStatusEnum {
    PENDING(0, "待支付"),
    SUCCESS(1, "已支付"),
    CANCELLED(2, "已取消");

    private final Integer status;
    private final String desc;

    RechargeStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}