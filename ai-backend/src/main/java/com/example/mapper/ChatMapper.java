package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.chatdto.Message;
import com.example.entity.chatvo.response.ResponseMessage;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMapper extends BaseMapper<Message>{
    /**
     * ORDER BY 子句中添加了 , role DESC 部分，它表示按照 created_at 升序排列，但是当时间相同时，按照 role 的降序排列（即 role = user 的数据优先
     **/

    @Select("SELECT role, content FROM db_message WHERE session_id = #{sessionId} ORDER BY created_at ASC,role DESC")
    @Results(id = "chatMessageResult", value = {
            @Result(column = "role", property = "role"),
            @Result(column = "content", property = "content")
    })
    List<ChatMessage> selectMessagesWithRoleAndContent(@Param("sessionId") String sessionID);



    @Select("SELECT role, content,created_at FROM db_message WHERE session_id = #{sessionId} ORDER BY created_at ASC,role DESC")
    @Results(id = "ResponseMessageResult", value = {
            @Result(column = "role", property = "role"),
            @Result(column = "content", property = "content"),
            @Result(column = "createdAt",property = "created_at")
    })
    List<ResponseMessage> getMessages(@Param("sessionId") String sessionID);

    @Delete("DELETE FROM db_message WHERE session_id = #{sessionId}")
    boolean deleteSession(@Param("sessionId") String sessionID);
}
