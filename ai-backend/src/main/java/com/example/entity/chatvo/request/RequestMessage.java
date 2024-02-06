package com.example.entity.chatvo.request;

import lombok.Data;
import org.apache.ibatis.jdbc.Null;

@Data
public class RequestMessage {

    private String role;
    private String content;
    private String session_Id= null;
    private String userid;
    private boolean IsContinuous;

}
