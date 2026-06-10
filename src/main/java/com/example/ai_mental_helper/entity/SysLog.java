package com.example.ai_mental_helper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_log")
public class SysLog {
    @TableId(type = IdType.AUTO)
    private Long logId;
    private String username;
    private String operation; // 操作描述
    private String method; // 请求方法路径
    private String params; // 请求参数 JSON
    private String ip; // 客户端 IP
    private Long executionTime; // 执行耗时（毫秒）
    private String exceptionMsg; // 异常堆栈
    private LocalDateTime createTime;
}