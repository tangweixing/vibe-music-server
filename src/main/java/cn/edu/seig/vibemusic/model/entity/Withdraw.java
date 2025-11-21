package cn.edu.seig.vibemusic.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户提现记录（含冻结流程）
 * </p>
 *
 * @author sunpingli
 * @since 2025-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_withdraw")
public class Withdraw implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    /**
     * 关联用户余额记录ID
     */
    @TableField("balance_id")
    private Long balanceId;

    @TableField("amount")
    private BigDecimal amount;

    /**
     * 提现方式：1-微信，2-支付宝
     */
    @TableField("withdraw_type")
    private Integer withdrawType;

    @TableField("account")
    private String account;

    @TableField("order_no")
    private String orderNo;

    /**
     * 状态：0-审核中（冻结中），1-已通过（冻结解除并扣减），2-已拒绝（冻结解除）
     */
    @TableField("status")
    private Integer status;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("audit_remark")
    private String auditRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private LocalDateTime updateTime;
}