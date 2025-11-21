package cn.edu.seig.vibemusic.enumeration;

import lombok.Getter;

// 提现状态枚举（关联冻结流程）
@Getter
public enum WithdrawStatusEnum {
    AUDITING(0, "审核中（冻结中）"),
    APPROVED(1, "已通过（冻结解除并扣减）"),
    REJECTED(2, "已拒绝（冻结解除）");

    private final Integer status;
    private final String desc;

    WithdrawStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}