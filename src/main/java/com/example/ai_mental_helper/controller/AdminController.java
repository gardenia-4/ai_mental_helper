package com.example.ai_mental_helper.controller;

import com.example.ai_mental_helper.common.Result;
import com.example.ai_mental_helper.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SysUserService userService;

    /**
     * 8. 用户审批激活与禁用接口
     * 请求体接收：{ "userId": 2, "status": 1 } -> 1激活，2禁用
     */
    @PutMapping("/user/status")
    public Result<?> changeUserStatus(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        Long statusValue = params.get("status");

        if (userId == null || statusValue == null) {
            return Result.error("参数传递不完整");
        }

        userService.updateUserStatus(userId, statusValue.intValue());
        return Result.success("审批状态修改成功！");
    }
}