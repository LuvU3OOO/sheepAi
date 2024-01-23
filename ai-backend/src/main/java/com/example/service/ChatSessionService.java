package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.chatdto.ChatSession;
import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.List;

public interface ChatSessionService extends IService<ChatSession> {

    public boolean updateToken(int tokens, String sessionId);

    public List<ChatSession> getSessions(String userid,long current, long size);

    public  boolean deleteSession(String sessionId);

    public boolean updateTime(String sessionId);
}
