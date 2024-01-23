package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.chatdto.ChatSession;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
    @Select("SELECT tokens FROM db_session WHERE session_id = #{sessionId}")
    Integer getToken(@Param("sessionId") Integer sessionID);
}
