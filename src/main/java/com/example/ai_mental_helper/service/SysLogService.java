package com.example.ai_mental_helper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ai_mental_helper.entity.SysLog;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
public interface SysLogService extends IService<SysLog> {
    // 异步保存系统日志，防止打印日志拖慢正常业务的响应速度
    void saveLogAsync(SysLog sysLog);
}