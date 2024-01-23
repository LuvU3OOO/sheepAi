package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.chatdto.Message;
import com.example.entity.chatvo.response.ResponseMessage;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChatMessageMapper extends BaseMapper<Message>{

    @Select("SELECT role, content FROM db_message WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    @Results(id = "chatMessageResult", value = {
            @Result(column = "role", property = "role"),
            @Result(column = "content", property = "content")
    })
    List<ChatMessage> selectMessagesWithRoleAndContent(@Param("sessionId") String sessionID);



    @Select("SELECT role, content,created_at FROM db_message WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    @Results(id = "ResponseMessageResult", value = {
            @Result(column = "role", property = "role"),
            @Result(column = "content", property = "content"),
            @Result(column = "createdAt",property = "created_at")
    })
    List<ResponseMessage> getMessages(@Param("sessionId") String sessionID);

    @Delete("DELETE FROM db_message WHERE session_id = #{sessionId}")
    boolean deleteSession(@Param("sessionId") String sessionID);
}
