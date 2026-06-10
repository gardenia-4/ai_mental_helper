package com.example.ai_mental_helper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ai_mental_helper.entity.UserEmotionLog;
import java.util.List;

public interface UserEmotionLogService extends IService<UserEmotionLog> {
    // 获取用户最近7天的情绪记录，用来喂给 ECharts 画折线图
    List<UserEmotionLog> getRecentSevenDaysScores(Long userId);
}