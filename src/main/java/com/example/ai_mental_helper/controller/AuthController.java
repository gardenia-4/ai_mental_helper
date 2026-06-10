package com.example.ai_mental_helper.controller;

import com.example.ai_mental_helper.annotation.LogAnnotation;
import com.example.ai_mental_helper.common.Result;
import com.example.ai_mental_helper.entity.SysUser;
import com.example.ai_mental_helper.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserService userService;

    /**
     * 1. 用户注册接口：新注册用户强制设置为待审核状态(0)
     * 改写后：统一返回标准 Result 对象，逻辑全部收拢在 Service 层中
     */
    @PostMapping("/register")
    @LogAnnotation(operation = "用户自主注册") // 🌟 AOP 审计点：无感记录注册行为
    public Result<?> register(@RequestBody SysUser user) {
        // 调用 Service 层业务，如果用户名重复会自动抛出 CustomException 被全局处理器捕获
        userService.register(user);
        return Result.success("注册成功，请等待管理员审核开通！");
    }

    /**
     * 2. 用户登录接口：对未审批的用户做硬拦截并派发 JWT Token
     * 改写后：登录成功会返回包含真实 token 和 user 信息的标准 Map 数据
     */
    @PostMapping("/login")
    @LogAnnotation(operation = "用户登录认证") // 🌟 AOP 审计点：无感记录登录行为
    public Result<?> login(@RequestBody SysUser loginRequest) {
        // 调用 Service 层业务，状态为0或2、或者密码错误时，Service 会直接抛出对应的 403 / 500 自定义异常
        Map<String, Object> data = userService.login(loginRequest);
        return Result.success(data);
    }
}