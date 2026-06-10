package com.example.ai_mental_helper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String nickname;
    private String role;
    private Integer status; // 0-待审核, 1-已激活, 2-已禁用 [cite: 4]
    private String avatar;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}