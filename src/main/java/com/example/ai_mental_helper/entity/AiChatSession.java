package com.example.ai_mental_helper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_chat_session")
public class AiChatSession {
    @TableId(type = IdType.AUTO)
    private Long sessionId;
    private Long userId;
    private String title;
    private Integer isDeleted; // 逻辑删除标记：0-正常, 1-已删除 [cite: 19]
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}