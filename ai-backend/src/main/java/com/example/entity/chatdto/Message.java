package com.example.entity.chatdto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_message")
@AllArgsConstructor
public class Message {

    @TableId(type = IdType.ASSIGN_UUID)
    private String message_id;
    private String userid;
    private String session_id;
    private String role;
    private String content;
    private Date createdAt;

}
