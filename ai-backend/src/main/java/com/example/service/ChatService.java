package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.chatdto.Message;
import com.example.entity.chatvo.response.ResponseMessage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.List;

public interface ChatService extends IService<Message> {
    public String createSession(String userid);
    public ChatCompletionRequest createChatRequest(List<ChatMessage> chatMessage);

    public boolean saveMessage(ChatMessage chatMessage, String userid, String sessionId);

    public void sendMessage(ChatMessage chatMessage,
                            String userid,
                            String sessionId,boolean IsContinuous);
    public List<ResponseMessage> getMessages(String sessionId,String userid);

    public void cancelChatRequest(String userid);


}
