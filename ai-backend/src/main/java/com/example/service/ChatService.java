package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.chatdto.Message;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.List;
import java.util.function.Supplier;

public interface ChatService extends IService<Message> {
    public String createSession(String userid,String topic);
    public ChatCompletionRequest createChatRequest(List<ChatMessage> chatMessage);

    public boolean saveMessage(ChatMessage chatMessage, String userid, String sessionId);

    public void sendMessage(ChatMessage chatMessage,
                            String userid,
                            String sessionId);
}
