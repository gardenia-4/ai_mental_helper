package com.example.ai_mental_helper.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ai_mental_helper.entity.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessage> {
}