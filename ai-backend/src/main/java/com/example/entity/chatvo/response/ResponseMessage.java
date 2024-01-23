package com.example.entity.chatvo.response;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseMessage {
    private String role;
    private String content;
    private Date createdAt;
}
