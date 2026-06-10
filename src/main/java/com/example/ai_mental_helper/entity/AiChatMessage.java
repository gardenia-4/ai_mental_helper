package com.example.ai_mental_helper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_chat_message")
public class AiChatMessage {
    @TableId(type = IdType.AUTO)
    private Long messageId;
    private Long sessionId;
    private String senderRole; // user 或 assistant
    private String content; // 经过 DFA 过滤后的文本 [cite: 33]
    private String rawContent; // 过滤前的原始文本，备用审计 [cite: 5]
    private LocalDateTime createTime;
}