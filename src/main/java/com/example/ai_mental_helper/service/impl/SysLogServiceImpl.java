package com.example.ai_mental_helper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ai_mental_helper.dao.SysLogMapper;
import com.example.ai_mental_helper.entity.SysLog;
import com.example.ai_mental_helper.service.SysLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Async // 开启异步执行，记得要在启动类上加 @EnableAsync 注解才能真正生效
    @Override
    public void saveLogAsync(SysLog sysLog) {
        this.baseMapper.insert(sysLog);
    }
}