package com.example.entity.chatvo.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class sessionResponse {
    String sessionId;

    String topic;

    Integer tokens;

    @TableField("created_at")
    Date createdAt;
}
