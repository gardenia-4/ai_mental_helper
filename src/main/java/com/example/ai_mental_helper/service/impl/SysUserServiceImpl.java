package com.example.ai_mental_helper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ai_mental_helper.common.JwtUtils;
import com.example.ai_mental_helper.dao.SysUserMapper;
import com.example.ai_mental_helper.entity.SysUser;
import com.example.ai_mental_helper.exception.CustomException;
import com.example.ai_mental_helper.service.SysUserService;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public void register(SysUser user) {
        // 6. 校验用户名是否存在
        Long count = this.baseMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, user.getUsername())
        );
        if (count > 0) {
            throw new CustomException(500, "该账号已存在，请更换用户名！");
        }

        // 刚性指标强制约束
        user.setStatus(0);  // 强制设置为 0 (待审核)
        user.setRole("USER"); // 注册默认是普通用户
        this.baseMapper.insert(user);
    }

    @Override
    public Map<String, Object> login(SysUser loginRequest) {
        // 7. 查询用户
        SysUser user = this.baseMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, loginRequest.getUsername())
        );

        // 校验密码（这里采用明文对比，若需要BCrypt可以自行替换）
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            throw new CustomException(500, "用户名或密码错误！");
        }

        // 核心硬拦截点：待审核状态拦截
        if (user.getStatus() == 0) {
            throw new CustomException(403, "您的账号正在审核中，请耐心等待管理员开通");
        }
        // 禁用状态拦截
        if (user.getStatus() == 2) {
            throw new CustomException(403, "您的账号已被禁用，请联系系统管理员");
        }

        // 验证通过，派发标准企业级 JWT Token
        String token = JwtUtils.generateToken(user.getUserId(), user.getUsername(), user.getRole());

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);
        return map;
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        // 8. 审批修改状态逻辑
        SysUser user = this.baseMapper.selectById(userId);
        if (user == null) {
            throw new CustomException(404, "未找到对应的用户记录");
        }
        user.setStatus(status);
        this.baseMapper.updateById(user);
    }
}