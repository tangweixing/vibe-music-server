package cn.edu.seig.vibemusic.util;

import cn.edu.seig.vibemusic.config.AlipaySandboxConfig;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝转账工具类（用于提现到用户支付宝账户）
 */
@Slf4j
@Component
public class AlipayTransferUtil {

    @Autowired
    private AlipaySandboxConfig alipaySandboxConfig;

    /**
     * 调用支付宝转账接口（提现到用户支付宝）
     * @param outBizNo 商户转账订单号（需唯一）
     * @param payeeAccount 收款方支付宝账号（用户的支付宝账号）
     * @param amount 转账金额
     * @param remark 转账备注
     * @return 转账是否成功
     */
    public  boolean transfer(String outBizNo, String payeeAccount, BigDecimal amount, String remark) {
        try {
            // 初始化支付宝客户端（沙箱环境）
            AlipayClient alipayClient = new DefaultAlipayClient(
                    alipaySandboxConfig.getServerUrl(),
                    alipaySandboxConfig.getAppId(),
                    alipaySandboxConfig.getPrivateKey(),
                    "json",
                    "UTF-8",
                    alipaySandboxConfig.getPublicKey(),
                    "RSA2"
            );

            // 创建转账请求
            AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_biz_no", outBizNo); // 商户唯一订单号
            bizContent.put("trans_amount", amount); // 转账金额（元）
            bizContent.put("product_code", "TRANS_ACCOUNT_NO_PWD"); // 支付宝转账产品码
            bizContent.put("biz_scene", "DIRECT_TRANSFER"); // 关键修改：使用DIRECT_TRANSFER

            // 收款方信息（支付宝账号）
            Map<String, Object> payeeInfo = new HashMap<>();
            payeeInfo.put("identity", payeeAccount); // 用户支付宝账号（如dhkquo0412@sandbox.com）
            payeeInfo.put("identity_type", "ALIPAY_LOGON_ID"); // 账号类型
            // 从提现记录中获取用户填写的姓名
            payeeInfo.put("name", "dhkquo0412");
            bizContent.put("payee_info", payeeInfo);

            bizContent.put("remark", remark); // 转账备注
            request.setBizContent(new com.alibaba.fastjson.JSONObject(bizContent).toString());

            // 执行转账
            AlipayFundTransUniTransferResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("支付宝转账成功，订单号：{}", outBizNo);
                return true;
            } else {
                log.error("支付宝转账失败，错误信息：{}", response.getMsg());
                return false;
            }
        } catch (AlipayApiException e) {
            log.error("支付宝转账接口调用异常", e);
            return false;
        }
    }
}