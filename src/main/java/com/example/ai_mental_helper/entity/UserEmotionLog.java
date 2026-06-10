package com.example.ai_mental_helper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_emotion_log")
public class UserEmotionLog {
    @TableId(type = IdType.AUTO)
    private Long logId;
    private Long userId;
    private Integer emotionScore; // 心情指数：1-5分 [cite: 22]
    private String notes; // 心情随笔 [cite: 22]
    private LocalDate logDate; // 打卡日期
    private LocalDateTime createTime;
}