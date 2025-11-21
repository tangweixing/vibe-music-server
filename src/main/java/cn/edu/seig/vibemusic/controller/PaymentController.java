package cn.edu.seig.vibemusic.controller;

import cn.edu.seig.vibemusic.model.dto.RechargeDTO;
import cn.edu.seig.vibemusic.model.dto.WithdrawDTO;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.IPaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
@Tag(name = "支付相关接口（充值/提现）")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/recharge")
    @Operation(summary="创建充值订单")
    public Result createRecharge(@Valid @RequestBody RechargeDTO rechargeDTO) {
        return paymentService.createRechargeOrder(rechargeDTO);
    }

    @PostMapping("/withdraw/apply")
    @Operation(summary="申请提现")
    public Result applyWithdraw(@Valid @RequestBody WithdrawDTO withdrawDTO) {
        return paymentService.applyWithdraw(withdrawDTO);
    }

    @PostMapping("/withdraw/audit")
    @Operation(summary="审核提现申请（管理员接口）")
    public Result auditWithdraw(@RequestParam Long withdrawId,
                                @RequestParam Integer status,
                                @RequestParam(required = false) String remark) {
        return paymentService.auditWithdraw(withdrawId, status, remark);
    }

    @GetMapping("/recharge/records")
    @Operation(summary="查询充值记录")
    public Result getRechargeRecords(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return paymentService.queryRechargeRecords(page, size);
    }

    @GetMapping("/withdraw/records")
    @Operation(summary="查询提现记录")
    public Result getWithdrawRecords(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return paymentService.queryWithdrawRecords(page, size);
    }

    @GetMapping("/balance")
    @Operation(summary="查询用户余额（可用+冻结）")
    public Result getUserBalance() {
        return paymentService.getUserBalance();
    }

    // 支付宝回调接口（对外开放，无需登录）
    @PostMapping("/alipay/callback")
    @Operation(summary="支付宝支付回调")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        return paymentService.alipayCallback(params);
    }

    // 微信回调接口（对外开放，无需登录）
    @PostMapping("/wechat/callback")
    @Operation(summary="微信支付回调")
    public String wechatCallback(@RequestBody String xmlData) {
        return paymentService.wechatCallback(xmlData);
    }
}