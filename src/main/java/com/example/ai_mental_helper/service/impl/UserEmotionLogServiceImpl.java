package com.example.ai_mental_helper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ai_mental_helper.dao.UserEmotionLogMapper;
import com.example.ai_mental_helper.entity.UserEmotionLog;
import com.example.ai_mental_helper.service.UserEmotionLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEmotionLogServiceImpl extends ServiceImpl<UserEmotionLogMapper, UserEmotionLog> implements UserEmotionLogService {

    @Override
    public List<UserEmotionLog> getRecentSevenDaysScores(Long userId) {
        return this.list(new LambdaQueryWrapper<UserEmotionLog>()
                .eq(UserEmotionLog::getUserId, userId)
                .orderByAsc(UserEmotionLog::getLogDate) // 按日期升序，方便图表从左到右渲染
                .last("LIMIT 7"));
    }
}