package com.example.ai_mental_helper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ai_mental_helper.entity.SysUser;
import java.util.Map;

public interface SysUserService extends IService<SysUser> {
    void register(SysUser user);
    Map<String, Object> login(SysUser loginRequest);
    void updateUserStatus(Long userId, Integer status);
}