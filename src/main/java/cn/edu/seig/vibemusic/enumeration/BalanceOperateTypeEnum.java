package cn.edu.seig.vibemusic.enumeration;

import lombok.Getter;

// 余额操作类型枚举（支持扩展转账、红包）
@Getter
public enum BalanceOperateTypeEnum {
    RECHARGE_ADD(1, "充值增加"),
    WITHDRAW_DEDUCT(2, "提现扣减"),
    TRANSFER_IN(3, "转账收入"),
    TRANSFER_OUT(4, "转账支出"),
    RED_PACKET_IN(5, "红包收入"),
    RED_PACKET_OUT(6, "红包支出"),
    FREEZE(7, "冻结"),
    UNFREEZE(8, "解冻");

    private final Integer type;
    private final String desc;

    BalanceOperateTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}