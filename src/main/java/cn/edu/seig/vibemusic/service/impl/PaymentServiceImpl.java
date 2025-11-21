package cn.edu.seig.vibemusic.service.impl;

import cn.edu.seig.vibemusic.config.AlipaySandboxConfig;
import cn.edu.seig.vibemusic.constant.JwtClaimsConstant;
import cn.edu.seig.vibemusic.constant.MessageConstant;
import cn.edu.seig.vibemusic.enumeration.*;
import cn.edu.seig.vibemusic.mapper.*;
import cn.edu.seig.vibemusic.model.dto.RechargeDTO;
import cn.edu.seig.vibemusic.model.dto.WithdrawDTO;
import cn.edu.seig.vibemusic.model.entity.*;
import cn.edu.seig.vibemusic.result.PageResult;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.IPaymentService;
import cn.edu.seig.vibemusic.util.AlipayTransferUtil;
import cn.edu.seig.vibemusic.util.OrderNumberUtil;
import cn.edu.seig.vibemusic.util.ThreadLocalUtil;
import cn.edu.seig.vibemusic.util.TypeConversionUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private RechargeMapper rechargeMapper;
    @Autowired
    private WithdrawMapper withdrawMapper;
    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private UserMapper userMapper;

    // 注入支付宝沙箱配置
    @Autowired
    private AlipaySandboxConfig alipaySandboxConfig;
    @Autowired
    private AlipayTransferUtil alipayTransferUtil;

/*    @Value("${alipay.sha-xiang.notify-url}")
    private String alipayNotifyUrl;
    @Value("${wechat.notify-url}")
    private String wechatNotifyUrl;*/

    /**
     * 获取当前用户的余额记录（不存在则初始化）
     */
    private UserBalance getOrInitUserBalance(Long userId) {
        UserBalance balance = userBalanceMapper.selectOne(new QueryWrapper<UserBalance>()
                .eq("user_id", userId));
        if (balance == null) {
            balance = new UserBalance()
                    .setUserId(userId)
                    .setAvailableBalance(BigDecimal.ZERO)
                    .setFrozenBalance(BigDecimal.ZERO)
                    .setVersion(0);
            balance.calculateTotalBalance();
            userBalanceMapper.insert(balance);
        }
        return balance;
    }


    // 创建充值订单
    @Override
    @Transactional
    public Result createRechargeOrder(RechargeDTO rechargeDTO) {
             // 从ThreadLocal获取当前登录用户信息
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Long userId = TypeConversionUtil.toLong(userMap.get(JwtClaimsConstant.USER_ID));
        UserBalance balance = getOrInitUserBalance(userId);

        String orderNo = OrderNumberUtil.generateOrderNo();

        Recharge recharge = new Recharge()
                .setUserId(userId)
                .setBalanceId(balance.getId())
                .setAmount(rechargeDTO.getAmount())
                .setPayType(rechargeDTO.getPayType())
                .setOrderNo(orderNo)
                .setStatus(RechargeStatusEnum.PENDING.getStatus())
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        rechargeMapper.insert(recharge);

        if (PayTypeEnum.ALIPAY.getType().equals(rechargeDTO.getPayType())) {
            return createAlipayOrder(orderNo, rechargeDTO.getAmount(), rechargeDTO.getReturnUrl());
        } else if (PayTypeEnum.WECHAT.getType().equals(rechargeDTO.getPayType())) {
            return createWechatOrder(orderNo, rechargeDTO.getAmount(), rechargeDTO.getReturnUrl());
        }
        return Result.error(MessageConstant.PAY_TYPE_ERROR);
    }

    // 支付宝支付（沙箱环境）
    private Result createAlipayOrder(String orderNo, BigDecimal amount, String returnUrl) {
        try {
            // 使用沙箱配置创建AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(
                    alipaySandboxConfig.getServerUrl(),
                    alipaySandboxConfig.getAppId(),
                    alipaySandboxConfig.getPrivateKey(),
                    "json",
                    "UTF-8",
                    alipaySandboxConfig.getPublicKey(),
                    "RSA2"
            );

            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setReturnUrl(returnUrl);
            request.setNotifyUrl(alipaySandboxConfig.getNotifyUrl());
            System.out.println(alipaySandboxConfig.getNotifyUrl());

            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);
            bizContent.put("total_amount", amount);
            bizContent.put("subject", "账户充值-" + orderNo);
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            request.setBizContent(new com.alibaba.fastjson.JSONObject(bizContent).toString());

            String form = alipayClient.pageExecute(request).getBody();
            return Result.success(form);
        } catch (AlipayApiException e) {
            return Result.error(MessageConstant.PAY_ORDER_CREATE_FAILED);
        }
    }

    // 微信支付（沙箱环境，简化示例）
    private Result createWechatOrder(String orderNo, BigDecimal amount, String returnUrl) {
        String payUrl = "weixin://wxpay/sandbox?out_trade_no=" + orderNo + "&total_fee=" + amount.multiply(new BigDecimal(100)).intValue();
        return Result.success(payUrl);
    }

    // 支付宝回调处理（充值到账逻辑）
    @Override
    @Transactional
    public String alipayCallback(Map<String, String> params) {
        System.out.println("test");
        String outTradeNo = params.get("out_trade_no");
        String tradeStatus = params.get("trade_status");

        Recharge recharge = rechargeMapper.selectOne(new QueryWrapper<Recharge>()
                .eq("order_no", outTradeNo));
        if (recharge == null) {
            return "fail";
        }

        if (RechargeStatusEnum.SUCCESS.getStatus().equals(recharge.getStatus())) {
            return "success";
        }
        System.out.println("TRADE_SUCCESS".equals(tradeStatus));
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            recharge.setStatus(RechargeStatusEnum.SUCCESS.getStatus())
                    .setPayTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            rechargeMapper.updateById(recharge);

            UserBalance balance = userBalanceMapper.selectById(recharge.getBalanceId());

            balance.setAvailableBalance(balance.getAvailableBalance().add(recharge.getAmount()));
            balance.calculateTotalBalance();
            int rows = userBalanceMapper.updateBalanceWithVersion(balance);
            if (rows == 0) {
                throw new RuntimeException("余额更新失败");
            }
        }

        return "success";
    }

    // 微信回调处理（类似支付宝，略）
    @Override
    @Transactional
    public String wechatCallback(String xmlData) {
        Map<String, String> params = parseWechatXml(xmlData);
        String outTradeNo = params.get("out_trade_no");
        String resultCode = params.get("result_code");

        Recharge recharge = rechargeMapper.selectOne(new QueryWrapper<Recharge>().eq("order_no", outTradeNo));
        if (recharge == null) {
            return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
        }

        if (RechargeStatusEnum.SUCCESS.getStatus().equals(recharge.getStatus())) {
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
        }

        if ("SUCCESS".equals(resultCode)) {
            recharge.setStatus(RechargeStatusEnum.SUCCESS.getStatus())
                    .setPayTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            rechargeMapper.updateById(recharge);

            UserBalance balance = userBalanceMapper.selectById(recharge.getBalanceId());

            balance.setAvailableBalance(balance.getAvailableBalance().add(recharge.getAmount()));
            balance.calculateTotalBalance();
            int rows = userBalanceMapper.updateBalanceWithVersion(balance);
            if (rows == 0) {
                throw new RuntimeException("余额更新失败");
            }
        }

        return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
    }

    // 解析微信回调XML（简化示例）
    private Map<String, String> parseWechatXml(String xmlData) {
        Map<String, String> map = new HashMap<>();
        map.put("out_trade_no", xmlData.contains("out_trade_no") ? xmlData.split("out_trade_no")[1].substring(0, 20) : "");
        map.put("result_code", "SUCCESS");
        return map;
    }

    // 申请提现（冻结金额逻辑）
    @Override
    @Transactional
    public Result applyWithdraw(WithdrawDTO withdrawDTO) {
             // 从ThreadLocal获取当前登录用户信息
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Long userId = TypeConversionUtil.toLong(userMap.get(JwtClaimsConstant.USER_ID));
        UserBalance balance = getOrInitUserBalance(userId);

        if (balance.getAvailableBalance().compareTo(withdrawDTO.getAmount()) < 0) {
            return Result.error(MessageConstant.BALANCE_INSUFFICIENT);
        }

        String orderNo = OrderNumberUtil.generateOrderNo();
        Withdraw withdraw = new Withdraw()
                .setUserId(userId)
                .setBalanceId(balance.getId())
                .setAmount(withdrawDTO.getAmount())
                .setWithdrawType(withdrawDTO.getWithdrawType())
                .setAccount(withdrawDTO.getAccount())
                .setOrderNo(orderNo)
                .setStatus(WithdrawStatusEnum.AUDITING.getStatus())
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        withdrawMapper.insert(withdraw);

        balance.setAvailableBalance(balance.getAvailableBalance().subtract(withdrawDTO.getAmount()));
        balance.setFrozenBalance(balance.getFrozenBalance().add(withdrawDTO.getAmount()));
        balance.calculateTotalBalance();
        int rows = userBalanceMapper.updateBalanceWithVersion(balance);
        if (rows == 0) {
            return Result.error(MessageConstant.WITHDRAW_APPLY_FAILED);
        }

        return Result.success(MessageConstant.WITHDRAW_APPLY_SUCCESS);
    }

    // 审核提现（处理冻结金额）
    @Override
    @Transactional
    public Result auditWithdraw(Long withdrawId, Integer status, String remark) {
        Withdraw withdraw = withdrawMapper.selectById(withdrawId);
        if (withdraw == null) {
            return Result.error(MessageConstant.WITHDRAW_RECORD_NOT_FOUND);
        }

        if (!WithdrawStatusEnum.AUDITING.getStatus().equals(withdraw.getStatus())) {
            return Result.error(MessageConstant.WITHDRAW_ALREADY_AUDITED);
        }

        UserBalance balance = userBalanceMapper.selectById(withdraw.getBalanceId());

        // 审核通过：调用支付宝/微信转账接口，成功后扣减冻结金额
        if (WithdrawStatusEnum.APPROVED.getStatus().equals(status)) {
            // 1. 检查冻结金额是否充足
            if (balance.getFrozenBalance().compareTo(withdraw.getAmount()) < 0) {
                return Result.error(MessageConstant.FROZEN_BALANCE_INSUFFICIENT);
            }

            // 2. 调用支付宝转账接口（根据提现方式判断：1-微信，2-支付宝）
            String outBizNo = "WITHDRAW_" + OrderNumberUtil.generateOrderNo(); // 生成转账订单号
            boolean transferSuccess = false;
            if (WithdrawTypeEnum.ALIPAY.getType().equals(withdraw.getWithdrawType())) {
                // 支付宝提现
                transferSuccess = alipayTransferUtil.transfer(
                        outBizNo,
                        withdraw.getAccount(), // 用户的支付宝账号
                        withdraw.getAmount(),
                        "用户提现：" + withdraw.getOrderNo()
                );
            } else if (WithdrawTypeEnum.WECHAT.getType().equals(withdraw.getWithdrawType())) {
                // 微信提现（需补充微信转账工具类，逻辑类似支付宝）
                // transferSuccess = wechatTransferUtil.transfer(...);
            }

            // 3. 转账成功后，才扣减冻结金额
            if (!transferSuccess) {
                return Result.error(MessageConstant.WITHDRAW_TRANSFER_FAILED); // 转账失败
            }

            // 4. 扣减冻结金额
            balance.setFrozenBalance(balance.getFrozenBalance().subtract(withdraw.getAmount()));
            balance.calculateTotalBalance();
            int rows = userBalanceMapper.updateBalanceWithVersion(balance);
            if (rows == 0) {
                return Result.error(MessageConstant.WITHDRAW_AUDIT_FAILED);
            }

        } else if (WithdrawStatusEnum.REJECTED.getStatus().equals(status)) {
            // 审核拒绝：解冻金额（退回可用余额）
            balance.setAvailableBalance(balance.getAvailableBalance().add(withdraw.getAmount()));
            balance.setFrozenBalance(balance.getFrozenBalance().subtract(withdraw.getAmount()));
            balance.calculateTotalBalance();
            int rows = userBalanceMapper.updateBalanceWithVersion(balance);
            if (rows == 0) {
                return Result.error(MessageConstant.WITHDRAW_AUDIT_FAILED);
            }
        }

        // 5. 更新提现记录状态
        withdraw.setStatus(status)
                .setAuditRemark(remark)
                .setAuditTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        withdrawMapper.updateById(withdraw);

        return Result.success(MessageConstant.WITHDRAW_AUDIT_SUCCESS);
    }

    // 查询用户余额
    @Override
    public Result getUserBalance() {
             // 从ThreadLocal获取当前登录用户信息
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Long userId = TypeConversionUtil.toLong(userMap.get(JwtClaimsConstant.USER_ID));
        UserBalance balance = getOrInitUserBalance(userId);
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("availableBalance", balance.getAvailableBalance());
        data.put("frozenBalance", balance.getFrozenBalance());
        data.put("totalBalance", balance.getTotalBalance());
        return Result.success(data);
    }

    // 查询充值记录（分页）
    @Override
    public Result queryRechargeRecords(Integer page, Integer size) {
             // 从ThreadLocal获取当前登录用户信息
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Long userId = TypeConversionUtil.toLong(userMap.get(JwtClaimsConstant.USER_ID));
        IPage<Recharge> iPage = new Page<>(page, size);
        IPage<Recharge> resultPage = rechargeMapper.selectPage(iPage, new QueryWrapper<Recharge>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));
        return Result.success(new PageResult<>(resultPage.getTotal(), resultPage.getRecords()));
    }

    // 查询提现记录（分页）
    @Override
    public Result queryWithdrawRecords(Integer page, Integer size) {
             // 从ThreadLocal获取当前登录用户信息
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Long userId = TypeConversionUtil.toLong(userMap.get(JwtClaimsConstant.USER_ID));
        IPage<Withdraw> iPage = new Page<>(page, size);
        IPage<Withdraw> resultPage = withdrawMapper.selectPage(iPage, new QueryWrapper<Withdraw>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));
        return Result.success(new PageResult<>(resultPage.getTotal(), resultPage.getRecords()));
    }
}