package cn.edu.seig.vibemusic.controller;

import cn.edu.seig.vibemusic.constant.MessageConstant;
import cn.edu.seig.vibemusic.model.dto.*;
import cn.edu.seig.vibemusic.model.vo.UserVO;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.IUserService;
import cn.edu.seig.vibemusic.service.MinioService;
import cn.edu.seig.vibemusic.util.BindingResultUtil;
import com.alipay.api.AlipayClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 用户相关接口控制器
 * </p>
 *
 * @author sunpingli
 * @since 2025-01-09
 */
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private MinioService minioService;

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     * @return 结果
     */
    @Operation(summary = "发送邮箱验证码", description = "用于注册、密码重置等场景的邮箱验证码发送")
    @GetMapping("/sendVerificationCode")
    public Result sendVerificationCode(
            @Parameter(description = "接收验证码的邮箱", example = "test@example.com")
            @RequestParam @Email String email) {
        return userService.sendVerificationCode(email);
    }

    /**
     * 邮箱注册
     *
     * @param userRegisterDTO 用户注册信息
     * @param bindingResult   参数校验结果
     * @return 结果
     */
    @Operation(summary = "邮箱注册", description = "通过邮箱验证码完成用户注册")
    @PostMapping("/register")
    public Result register(
            @Parameter(description = "用户注册信息（包含邮箱、密码、验证码等）")
            @RequestBody @Valid UserRegisterDTO userRegisterDTO,
            BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        boolean isCodeValid = userService.verifyVerificationCode(userRegisterDTO.getEmail(), userRegisterDTO.getVerificationCode());
        if (!isCodeValid) {
            return Result.error(MessageConstant.VERIFICATION_CODE + MessageConstant.INVALID);
        }

        return userService.register(userRegisterDTO);
    }

    /**
     * 账号密码登录
     *
     * @param userLoginDTO  用户登录信息
     * @param bindingResult 参数校验结果
     * @return 结果
     */
    @Operation(summary = "邮箱密码登录", description = "通过用户名/邮箱和密码登录系统")
    @PostMapping("/login")
    public Result login(
            @Parameter(description = "登录信息（包含用户名/邮箱、密码）")
            @RequestBody @Valid UserLoginDTO userLoginDTO,
            BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        return userService.login(userLoginDTO);
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的基本信息")
    @GetMapping("/getUserInfo")
    public Result<UserVO> getUserInfo() {
        return userService.userInfo();
    }

    /**
     * 更新用户基本信息
     *
     * @param userDTO       用户信息
     * @param bindingResult 参数校验结果
     * @return 结果
     */
    @Operation(summary = "更新用户信息", description = "修改当前登录用户的基本资料（如昵称、性别等）")
    @PutMapping("/updateUserInfo")
    public Result updateUserInfo(
            @Parameter(description = "待更新的用户信息")
            @RequestBody @Valid UserDTO userDTO,
            BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        return userService.updateUserInfo(userDTO);
    }

    /**
     * 更新用户头像
     *
     * @param avatar 头像文件
     * @return 结果
     */
    @Operation(summary = "更新用户头像", description = "上传并更新当前登录用户的头像")
    @PatchMapping("/updateUserAvatar")
    public Result updateUserAvatar(
            @Parameter(description = "头像图片文件（支持jpg、png等格式）")
            @RequestParam("avatar") MultipartFile avatar) {
        String avatarUrl = minioService.uploadFile(avatar, "users");
        return userService.updateUserAvatar(avatarUrl);
    }

    /**
     * 更新用户密码（需验证原密码）
     *
     * @param userPasswordDTO 密码信息
     * @param token           认证token
     * @param bindingResult   参数校验结果
     * @return 结果
     */
    @Operation(summary = "更新密码", description = "已登录状态下修改密码（需验证原密码）")
    @PatchMapping("/updateUserPassword")
    public Result updateUserPassword(
            @Parameter(description = "密码信息（包含原密码、新密码）")
            @RequestBody @Valid UserPasswordDTO userPasswordDTO,
            @Parameter(description = "登录凭证token")
            @RequestHeader("Authorization") String token,
            BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        return userService.updateUserPassword(userPasswordDTO, token);
    }

    /**
     * 重置用户密码（通过邮箱验证码）
     *
     * @param userResetPasswordDTO 密码重置信息
     * @param bindingResult        参数校验结果
     * @return 结果
     */
    @Operation(summary = "重置密码", description = "通过邮箱验证码找回并重置密码")
    @PatchMapping("/resetUserPassword")
    public Result resetUserPassword(
            @Parameter(description = "密码重置信息（包含邮箱、验证码、新密码）")
            @RequestBody @Valid UserResetPasswordDTO userResetPasswordDTO,
            BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        boolean isCodeValid = userService.verifyVerificationCode(userResetPasswordDTO.getEmail(), userResetPasswordDTO.getVerificationCode());
        if (!isCodeValid) {
            return Result.error(MessageConstant.VERIFICATION_CODE + MessageConstant.INVALID);
        }

        return userService.resetUserPassword(userResetPasswordDTO);
    }

    /**
     * 用户登出
     *
     * @param token 认证token
     * @return 结果
     */
    @Operation(summary = "用户登出", description = "注销当前用户的登录状态")
    @PostMapping("/logout")
    public Result logout(
            @Parameter(description = "登录凭证token")
            @RequestHeader("Authorization") String token) {
        return userService.logout(token);
    }

    /**
     * 注销账号
     *
     * @return 结果
     */
    @Operation(summary = "注销账号", description = "永久删除当前用户的账号及相关数据")
    @DeleteMapping("/deleteAccount")
    public Result deleteAccount() {
        return userService.deleteAccount();
    }

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @return 结果
     */
    @Operation(summary = "发送手机验证码", description = "用于手机号注册、登录的验证码发送")
    @GetMapping("/sendPhoneVerificationCode")
    public Result sendPhoneVerificationCode(
            @Parameter(description = "接收验证码的手机号", example = "13800138000")
            @RequestParam @Pattern(regexp = "^1[3456789]\\d{9}$",
                    message = MessageConstant.PHONE + MessageConstant.FORMAT_ERROR) String phone) {
        return userService.sendPhoneVerificationCode(phone);
    }

    /**
     * 手机号注册
     *
     * @param userPhoneRegisterDTO 手机号注册信息
     * @param bindingResult        参数校验结果
     * @return 结果
     */
    @Operation(summary = "手机号注册", description = "通过手机号+验证码+密码完成注册")
    @PostMapping("/phoneRegister")
    public Result phoneRegister(
            @Parameter(description = "手机号注册信息（包含手机号、密码、验证码）")
            @RequestBody @Valid UserPhoneRegisterDTO userPhoneRegisterDTO,
            BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        return userService.phoneRegister(userPhoneRegisterDTO);
    }

    /**
     * 手机号密码登录
     *
     * @param userPhoneLoginDTO 手机号登录信息
     * @param bindingResult     参数校验结果
     * @return 结果
     */
    @Operation(summary = "手机号密码登录", description = "通过手机号和密码登录系统")
    @PostMapping("/phoneLogin")
    public Result phoneLogin(
            @Parameter(description = "手机号登录信息（包含手机号、密码）")
            @RequestBody @Valid UserPhoneLoginDTO userPhoneLoginDTO,
            BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        return userService.phoneLogin(userPhoneLoginDTO);
    }

    /**
     * 手机号验证码登录
     *
     * @param userPhoneCodeLoginDTO 手机号验证码登录信息
     * @param bindingResult         参数校验结果
     * @return 结果
     */
    @Operation(summary = "手机号验证码登录", description = "通过手机号和验证码快速登录系统")
    @PostMapping("/phoneCodeLogin")
    public Result phoneCodeLogin(
            @Parameter(description = "手机号验证码登录信息（包含手机号、验证码）")
            @RequestBody @Valid UserPhoneCodeLoginDTO userPhoneCodeLoginDTO,
            BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        return userService.phoneCodeLogin(userPhoneCodeLoginDTO);
    }


    @Operation(summary = "微信登录", description = "通过微信授权登录系统")
    @PostMapping("/wxLogin")
    public Result wxLogin(@RequestBody @Valid WxLoginDTO wxLoginDTO) {
        return userService.wxLogin(wxLoginDTO.getCode());
    }

    @Operation(summary = "支付宝登录", description = "通过支付宝授权登录系统")
    @PostMapping("/alipayLogin")
    public Result alipayLogin(@RequestBody @Valid AlipayLoginDTO alipayLoginDTO) {
        return userService.alipayLogin(alipayLoginDTO.getAuthCode());
    }
}