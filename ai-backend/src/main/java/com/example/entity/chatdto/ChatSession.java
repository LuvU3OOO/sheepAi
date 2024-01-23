package com.example.entity.chatdto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.dto.Account;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.util.Date;
import java.util.List;


@Data
@TableName("db_session")
@AllArgsConstructor
public class ChatSession {

    @TableId(type = IdType.ASSIGN_UUID)
    String sessionId;

    String userid;

    String topic;

    Integer tokens;

    Date createdAt;
}
