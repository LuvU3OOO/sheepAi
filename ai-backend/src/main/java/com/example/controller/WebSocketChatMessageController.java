package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.chatdto.ChatSession;
import com.example.entity.chatvo.request.RequestMessage;
import com.example.service.ChatService;
import com.example.service.ChatSessionService;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.annotation.Resource;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class WebSocketChatMessageController {

    @Resource
    ChatService chatMessageService;

    @Resource
    ChatSessionService sessionService;

    @GetMapping("/ChatMessage/getSession")
    public RestBean<List<ChatSession>> getSessions(@RequestParam String userid, @RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "100") long pageSize) {
        List<ChatSession> sessions = sessionService.getSessions(userid, pageNum, pageSize);
        return RestBean.success(sessions);
    }

    @PostMapping("/chatMessage/send")
    public RestBean<Void> chat(@RequestBody RequestMessage requestMessage) {

        ChatMessage message = new ChatMessage(requestMessage.getRole(), requestMessage.getContent());

        if (requestMessage.getSession_Id() == null) {
            chatMessageService.sendMessage(message, requestMessage.getUserid(), null);
        } else {
            chatMessageService.sendMessage(message, requestMessage.getUserid(), requestMessage.getSession_Id());
        }
        return RestBean.success();
    }

    @GetMapping("/ChatMessage/deleteSession")
    public RestBean<String> deleteSessions(@RequestParam String sessionId){
        sessionService.deleteSession(sessionId);
        return RestBean.success("删除成功");
    }
}
