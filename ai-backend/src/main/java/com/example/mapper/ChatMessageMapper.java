package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.chatdto.Message;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChatMessageMapper extends BaseMapper<Message>{

    @Select("SELECT role, content FROM db_message WHERE userid = #{userid} and session_id = #{sessionId}")
    @Results(id = "chatMessageResult", value = {
            @Result(column = "role", property = "role"),
            @Result(column = "content", property = "content")
    })
    List<ChatMessage> selectMessagesWithRoleAndContent(@Param("userid") String userid, @Param("sessionId") String sessionID);
}
