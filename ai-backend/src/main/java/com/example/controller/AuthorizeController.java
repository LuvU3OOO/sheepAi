package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("/api/auth")
@Tag(name = "登录校验相关", description = "包括用户登录、注册、验证码请求等操作。")
public class AuthorizeController {
    @Resource
    AccountService service;


    /**
     * 请求邮件验证码
     * @param email 请求邮件
     * @param type 类型
     * @param request 请求
     * @return 是否请求成功
     */
    @GetMapping("/ask-code")
    @Operation(summary = "请求邮件验证码")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp= "(register|reset)") String type,
                                        HttpServletRequest request){

        return this.messageHandle(() ->
                service.registerEmailVerifyCode(type,email,request.getRemoteAddr()));
    }

    // 这里统一开发标准get请求用参数接收，post用对象接收
    /**
     * 进行用户注册操作，需要先请求邮件验证码
     * @param vo 注册信息
     * @return 是否注册成功
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册操作")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){

        return this.messageHandle(vo,service::registerEmailAccount);
    }

    /**
     * 执行密码重置确认，检查验证码是否正确
     * @param vo 密码重置信息
     * @return 是否操作成功
     */
    @PostMapping("/reset-confirm")
    @Operation(summary = "密码重置确认")
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){
        return this.messageHandle(vo,service::resetConfirm);

    }

    /**
     * 执行密码重置操作
     * @param vo 密码重置信息
     * @return 是否操作成功
     */
    @PostMapping("/reset-password")
    @Operation(summary = "密码重置操作")
    public RestBean<Void> resetConfirm(@RequestBody @Valid EmailResetVO vo){
        return this.messageHandle(vo,service::resetEmailAccountPassword);

    }



    /**
     * 针对于返回值为String作为错误信息的方法进行统一处理
     * @return 响应结果
     * @param <T> 响应结果类型
     */
    private <T> RestBean<Void> messageHandle(T vo, Function<T, String> fun){
        return  messageHandle(() ->fun.apply(vo));
    }

    /**
     * 针对于返回值为String作为错误信息的方法进行统一处理
     * @param action 具体操作
     * @return 响应结果
     */
    private RestBean<Void> messageHandle(Supplier<String> action){
        String message = action.get();
        return message==null ? RestBean.success() : RestBean.failure(400,message);
    }


}
