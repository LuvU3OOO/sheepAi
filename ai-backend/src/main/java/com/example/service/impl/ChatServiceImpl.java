package com.example.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.chatdto.ChatSession;
import com.example.entity.chatdto.Message;
import com.example.entity.chatvo.response.ResponseMessage;
import com.example.mapper.ChatMapper;
import com.example.service.ChatService;
import com.example.service.ChatSessionService;
import com.example.utils.ChatUtils;
import com.example.websocket.MyWebSocketHandler1;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.swagger.v3.core.util.Json;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Message> implements ChatService {


    @Resource
    ChatSessionService sessionService;

    @Resource
    OpenAiService service;

    @Resource
    MyWebSocketHandler1 myWebSocketHandler1;

    @Resource
    ChatUtils chatUtils;

    @Resource
    StringRedisTemplate stringRedisTemplate;


//    private volatile boolean isCancelled = false;

    private static final Map<String, Boolean> isCancelledMap = new ConcurrentHashMap<>();

    @Override
    public String createSession(String userid) {
        ChatSession session = new ChatSession(null, userid, "新对话", 0, new Date());
        sessionService.save(session);
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant.");
        saveMessage(systemMessage, userid, session.getSessionId());
        ChatMessage newMessage = new ChatMessage(ChatMessageRole.ASSISTANT.value(), "请您提出问题，我将尽可能为你解答。");
        saveMessage(newMessage, userid, session.getSessionId());
        // 插入后，session 对象中的 session_id 已经被 MyBatis-Plus 自动生成
        return session.getSessionId();
    }


    /**
     * 构造ChatGPT请求参数
     *
     * @param chatMessageList 用户的发送内容
     * @return 返回包含用户发送内容+配置信息的ChatGPT请求参数。
     */
    @Override
    public ChatCompletionRequest createChatRequest(List<ChatMessage> chatMessageList) {

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo-0613")
                .messages(chatMessageList)
                .n(1)
                .maxTokens(1024)
                .logitBias(new HashMap<>())
                .build();

        log.info("请求体：{}", chatCompletionRequest);
        return chatCompletionRequest;
    }

    @Override
    public boolean saveMessage(ChatMessage chatMessage, String userid, String sessionId) {
        Message message = new Message(null, userid, sessionId, chatMessage.getRole(), chatMessage.getContent(), new Date());
        return this.save(message);
    }

    public void flowGptRequest(String userid, String session_id, List<ChatMessage> Messages, ChatMessage chatMessage, boolean isContinuous) {

        Flowable<ChatCompletionChunk> flowable = service.streamChatCompletion(createChatRequest(Messages));



        ChatMessage responesChatMessage = service.mapStreamToAccumulator(flowable)
                .takeUntil(accumulator -> (isCancelledMap.getOrDefault(userid, false))) //中止响应
                .doOnNext(accumulator -> {
                    if (accumulator.getMessageChunk().getContent() != null) {
                        myWebSocketHandler1.sendMessageToUser(new TextMessage(accumulator.getMessageChunk().getContent()), userid.toString());

                    }
                })
                .doOnComplete(System.out::println)
                .lastElement()
                .blockingGet()
                .getAccumulatedMessage();

        if (!isCancelledMap.get(userid)) {
            if (saveMessage(chatMessage, userid, session_id)) {
                saveMessage(responesChatMessage, userid, session_id);
                sessionService.updateTime(session_id);
                if (isContinuous) {
                    stringRedisTemplate.opsForList().rightPush(userid, JSON.toJSONString(responesChatMessage));
                }
                Messages.add(responesChatMessage);
                int tokens = chatUtils.CalculateToken(Messages);
                if (sessionService.updateToken(tokens, session_id)) {
                    System.out.println("本次回答消耗token:" + tokens);
                }
            }
        }
        isCancelledMap.put(userid,false);
    }

    @Override
    public void sendMessage(ChatMessage chatMessage,
                            String userid,
                            String session_id, boolean isContinuous) {

        isCancelledMap.put(userid,false);
        List<ChatMessage> ChatMessageList = new ArrayList<>();
        List<String> jsonMessages = stringRedisTemplate.opsForList().range(userid, 0, -1);
        // 如果队列长度大于10，只取最后10个元素
        if(jsonMessages.size() == 2){
            if(chatMessage.getContent().length()>15){
                sessionService.updateTopic(chatMessage.getContent().substring(0,15),session_id);
            }else{
                sessionService.updateTopic(chatMessage.getContent(),session_id);
            }
        }

        if (jsonMessages.size() > 10) {
            jsonMessages = jsonMessages.subList(jsonMessages.size() - 10, jsonMessages.size());
        }
        ChatMessageList = jsonMessages.stream()
                .map(jsonMessage -> JSON.parseObject(jsonMessage, ChatMessage.class))
                .collect(Collectors.toList());
        System.out.println(userid + ChatMessageList);

        if (isContinuous) {
            ChatMessageList.add(chatMessage);
            //流式请求
            try {
                flowGptRequest(userid, session_id, ChatMessageList, chatMessage, isContinuous);
            } catch (OpenAiHttpException e) {
                service.shutdownExecutor();
                service = chatUtils.ChangeApikey();
                flowGptRequest(userid, session_id, ChatMessageList, chatMessage, isContinuous);
            }
        } else {
            List<ChatMessage> onceChatMessageList = new ArrayList<>();
            ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant.");
            onceChatMessageList.add(systemMessage);
            onceChatMessageList.add(chatMessage);
            //流式请求
            try {
                flowGptRequest(userid, session_id, onceChatMessageList, chatMessage, isContinuous);
            } catch (OpenAiHttpException e) {
                service.shutdownExecutor();
                service = chatUtils.ChangeApikey();
                flowGptRequest(userid, session_id, onceChatMessageList, chatMessage, isContinuous);
            }
        }

    }

    @Override
    public List<ResponseMessage> getMessages(String sessionId, String userid) {
        List<ResponseMessage> Messages;
        Messages = this.baseMapper.getMessages(sessionId);
        List<ChatMessage> messagesList = this.baseMapper.selectMessagesWithRoleAndContent(sessionId);

        List<String> jsonMessages = messagesList.stream()
                .map(JSON::toJSONString)
                .toList();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(userid))) {
            stringRedisTemplate.delete(userid);
        }
        stringRedisTemplate.opsForList().rightPushAll(userid, jsonMessages.toArray(new String[0]));
        stringRedisTemplate.expire(userid, 300, TimeUnit.SECONDS);

        return Messages;
    }


    @Override
    // 添加此方法以允许前端取消请求
    public void cancelChatRequest(String userid) {
       isCancelledMap.put(userid,true);

        System.out.println(isCancelledMap.get(userid));
    }
}
