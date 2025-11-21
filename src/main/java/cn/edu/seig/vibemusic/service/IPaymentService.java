package cn.edu.seig.vibemusic.service;

import cn.edu.seig.vibemusic.model.dto.RechargeDTO;
import cn.edu.seig.vibemusic.model.dto.WithdrawDTO;
import cn.edu.seig.vibemusic.result.Result;
import java.util.Map;

public interface IPaymentService {
    // 创建充值订单
    Result createRechargeOrder(RechargeDTO rechargeDTO);

    // 支付宝回调处理
    String alipayCallback(Map<String, String> params);

    // 微信回调处理
    String wechatCallback(String xmlData);

    // 申请提现（含冻结金额）
    Result applyWithdraw(WithdrawDTO withdrawDTO);

    // 审核提现（通过/拒绝，处理冻结金额）
    Result auditWithdraw(Long withdrawId, Integer status, String remark);

    // 查询充值记录
    Result queryRechargeRecords(Integer page, Integer size);

    // 查询提现记录
    Result queryWithdrawRecords(Integer page, Integer size);

    // 查询用户余额（可用+冻结）
    Result getUserBalance();
}