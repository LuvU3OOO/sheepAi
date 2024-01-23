package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.chatdto.ChatSession;
import com.example.mapper.ChatMessageMapper;
import com.example.mapper.ChatSessionMapper;
import com.example.service.ChatService;
import com.example.service.ChatSessionService;
import com.example.utils.ChatUtils;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatSessionImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatSessionService {

    @Resource
    ChatMessageMapper messageMapper;

    @Override
    public boolean updateToken(int tokens, String sessionId) {
        return this.update().eq("session_id",sessionId).set("tokens",tokens).update();
    }

    @Override
    public List<ChatSession> getSessions(String userid,long current, long size) {

        // 创建分页对象
        Page<ChatSession> page = new Page<>(current, size);
        return this.query().eq("userid",userid).orderByDesc("created_at").page(page).getRecords();
    }

    @Override
    public boolean deleteSession(String sessionId) {

        return this.removeById(sessionId)&&messageMapper.deleteSession(sessionId);
    }

    @Override
    public boolean updateTime(String sessionId) {
        return this.update().eq("session_id",sessionId).set("created_at",new Date()).update();
    }
}
