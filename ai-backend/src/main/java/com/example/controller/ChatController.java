package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.chatdto.ChatSession;
import com.example.entity.chatvo.request.RequestMessage;
import com.example.entity.chatvo.response.ResponseMessage;
import com.example.service.ChatService;
import com.example.service.ChatSessionService;
import com.example.utils.ChatUtils;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    @Resource
    ChatService chatMessageService;

    @Resource
    ChatSessionService sessionService;

    @Resource
    ChatUtils chatUtils;



    @GetMapping("/getSession")
    public RestBean<List<ChatSession>> getSessions(@RequestParam String userid, @RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "100") long pageSize) {
        List<ChatSession> sessions = sessionService.getSessions(userid, pageNum, pageSize);
        return RestBean.success(sessions);
    }

    @PostMapping("/send")
    public RestBean<Void> chat(@RequestBody RequestMessage requestMessage) {

        ChatMessage message = new ChatMessage(requestMessage.getRole(), requestMessage.getContent());
        try{
            if (requestMessage.getSession_Id() == null) {
                chatMessageService.sendMessage(message, requestMessage.getUserid(), null, requestMessage.isIsContinuous());
                System.out.println("requestMessage.isIsContinuous()"+requestMessage.isIsContinuous());
            } else {
                chatMessageService.sendMessage(message, requestMessage.getUserid(), requestMessage.getSession_Id(),requestMessage.isIsContinuous());
            }
        }catch (Exception e){
            if (e.getMessage() != null && e.getMessage().contains("Connect timed out")) {
                // 这是一个超时异常
                return RestBean.failure(408, "服务器网络异常，请联系管理员");
            } else {
                // 其他类型的 ResourceAccessException
                return RestBean.failure(500, "其他网络异常");
            }
        }

        return RestBean.success();
    }

    @GetMapping("/deleteSession")
    public RestBean<String> deleteSessions(@RequestParam String sessionId){
        sessionService.deleteSession(sessionId);
        return RestBean.success("删除成功");
    }
    @GetMapping("/getMessages")
    public RestBean<List<ResponseMessage>> getMessages(@RequestParam String sessionId,@RequestParam String userid) {
        List<ResponseMessage> messages = chatMessageService.getMessages(sessionId,userid);
        return RestBean.success(messages);
    }

    @GetMapping("/cancel")
    public RestBean<Void> cancelRequest(@RequestParam String userid) {
        chatMessageService.cancelChatRequest(userid);
        return RestBean.success();
    }

    @GetMapping("/createSession")
    public RestBean<String> createSession(@RequestParam String userid) {

        return RestBean.success(chatMessageService.createSession(userid));
    }
}
