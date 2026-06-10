package com.example.ai_mental_helper.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ai_mental_helper.entity.UserEmotionLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserEmotionLogMapper extends BaseMapper<UserEmotionLog> {
}