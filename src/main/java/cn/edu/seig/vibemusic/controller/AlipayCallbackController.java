package cn.edu.seig.vibemusic.controller;

import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.IUserService;
import cn.edu.seig.vibemusic.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 支付宝登录回调控制器
 */
@RestController
@RequestMapping("/api/oauth")
@Slf4j
public class AlipayCallbackController {

    @Autowired
    private IUserService authService; // 注入你的授权服务（包含alipayLogin方法的类）
    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate; // 用于验证state参数

    /**
     * 支付宝授权回调接口
     * 支付宝会以GET方式请求该接口，并携带code（即auth_code）和state参数
     *
     * @param code  支付宝返回的授权临时票据（auth_code）
     * @param state 客户端请求时传入的状态参数（用于防CSRF，需验证）
     * @return 登录成功后重定向到前端页面（携带token或提示信息）
     */
    @GetMapping("/alipay/callback")
    public RedirectView alipayCallback(
            @RequestParam("auth_code") String code,
            @RequestParam(value = "state", required = false) String state) {

        try {
            // 1. 验证state参数（防CSRF攻击，根据实际业务实现）
            // 例如：检查state是否与前端发起授权时存入Redis的state一致
       /*     if (!validateState(state)) {
                log.warn("支付宝回调state验证失败，state: {}", state);
                // 验证失败，重定向到前端登录页并提示错误
                return new RedirectView("http://localhost:8090/login?error=非法请求");
            }*/

            // 2. 调用已实现的alipayLogin方法，传入auth_code获取登录结果
            Result result = authService.alipayLogin(code);
            System.out.println(result);
            // 3. 根据登录结果处理重定向
            if (result.getCode() == 0) {
                // 登录成功，将token传递给前端（前端可存入localStorage）
                String token = (String) result.getData();
                return new RedirectView("http://localhost:8090?token=" + token);
            } else {
                // 登录失败，携带错误信息重定向
                return new RedirectView("http://localhost:8090/login?error=" + result.getMessage());
            }

        } catch (Exception e) {
            log.error("支付宝回调处理失败", e);
            // 异常情况重定向到登录页
            return new RedirectView("http://localhost:8090/login?error=登录失败，请重试");
        }
    }

    /**
     * 验证state参数（关键：防CSRF攻击）
     * 实现逻辑：
     * 1. 前端发起支付宝授权前，生成随机state并存入Redis（设置短期过期时间，如5分钟）
     * 2. 回调时验证传入的state是否在Redis中存在且未过期
     */
    private boolean validateState(String state) {
        if (state == null || state.trim().isEmpty()) {
            return false;
        }
        // 从Redis获取前端存入的state（假设key为"alipay_state:" + state）
        String storedState = stringRedisTemplate.opsForValue().get("alipay_state:" + state);
        if (storedState == null) {
            return false;
        }
        // 验证通过后删除Redis中的state（防止重复使用）
        stringRedisTemplate.delete("alipay_state:" + state);
        return true;
    }
}