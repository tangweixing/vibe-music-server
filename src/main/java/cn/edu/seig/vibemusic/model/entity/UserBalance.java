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
 * 用户余额表（支持冻结、转账、红包等扩展）
 * </p>
 *
 * @author sunpingli
 * @since 2025-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_balance")
public class UserBalance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（关联tb_user表）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 可用余额
     */
    @TableField("available_balance")
    private BigDecimal availableBalance;

    /**
     * 冻结金额（用于提现审核、红包未领取等）
     */
    @TableField("frozen_balance")
    private BigDecimal frozenBalance;

    /**
     * 总余额（可用+冻结，冗余字段）
     */
    @TableField("total_balance")
    private BigDecimal totalBalance;

    /**
     * 乐观锁版本号（防并发问题）
     */
    @TableField("version")
    private Integer version;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 自动计算总余额（可用+冻结）
     */
    public void calculateTotalBalance() {
        this.totalBalance = this.availableBalance.add(this.frozenBalance);
    }
}