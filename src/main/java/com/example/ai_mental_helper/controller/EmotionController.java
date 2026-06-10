package com.example.ai_mental_helper.controller;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ai_mental_helper.common.Result; // 确保和你项目里的统一返回类路径一致
import com.example.ai_mental_helper.entity.UserEmotionLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emotion")
public class EmotionController {

    @Autowired
    private IService<UserEmotionLog> emotionLogService; // 注入通用Service

    /**
     * 1. 用户每日心情打卡落库接口
     */
    @PostMapping("/check-in")
    public Result<?> checkIn(@RequestBody UserEmotionLog log) {
        // 校验心情指数是否在1-5分之间
        if (log.getEmotionScore() == null || log.getEmotionScore() < 1 || log.getEmotionScore() > 5) {
            return Result.error("心情指数必须在 1-5 之间！");
        }

        // 自动补全时间和日期
        log.setLogDate(LocalDate.now());
        log.setCreateTime(LocalDateTime.now());

        boolean saved = emotionLogService.save(log);
        return saved ? Result.success("打卡成功") : Result.error("打卡失败");
    }

    /**
     * 2. 查询近 7 天历史打卡分数接口（提供给前端同学 B 画 ECharts 折线图）
     */
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getTrend(@RequestParam Long userId) {
        // 计算 7 天前的边界
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // 查询该用户近 7 天的数据，并按时间升序排列
        List<UserEmotionLog> list = emotionLogService.lambdaQuery()
                .eq(UserEmotionLog::getUserId, userId)
                .ge(UserEmotionLog::getCreateTime, sevenDaysAgo)
                .orderByAsc(UserEmotionLog::getCreateTime)
                .list();

        // 转化为 ECharts 最喜欢的轻量格式：[{"date": "06-09", "score": 4, "note": "..."}, ...]
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<Map<String, Object>> chartData = list.stream().map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", log.getCreateTime().format(formatter));
            map.put("score", log.getEmotionScore());
            map.put("note", log.getNotes());
            return map;
        }).collect(Collectors.toList());

        return Result.success(chartData);
    }
}