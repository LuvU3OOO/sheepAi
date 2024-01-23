package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.chatdto.ChatSession;
import com.example.entity.chatdto.Message;
import com.example.mapper.ChatMessageMapper;
import com.example.service.ChatService;
import com.example.service.ChatSessionService;
import com.example.utils.ChatUtils;
import com.example.websocket.MyWebSocketHandler1;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Flowable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.util.*;



@Service
@Slf4j
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, Message> implements ChatService {

    @Resource
    ChatMessageMapper chatMapper;

    @Resource
    ChatSessionService sessionService;

    @Resource
    OpenAiService service;

    @Resource
    MyWebSocketHandler1 myWebSocketHandler1;

    @Resource
    ChatUtils chatUtils;



    @Override
    public String createSession(String userid,String topic) {
        ChatSession session = new ChatSession(null,userid,topic,0,new Date());
        sessionService.save(session);
        System.out.println(session.getSessionId());
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
                .maxTokens(256)
                .logitBias(new HashMap<>())
                .build();

        log.info("请求体：{}", chatCompletionRequest);
        return chatCompletionRequest;
    }

    @Override
    public boolean saveMessage(ChatMessage chatMessage, String userid, String sessionId) {
        Message message = new Message(null,userid,sessionId,chatMessage.getRole(),chatMessage.getContent(),new Date());
        return this.save(message);
    }

    @Override
    public void sendMessage(ChatMessage chatMessage,
                            String userid,
                            String session_id){

        List<ChatMessage> chatMessageList = new ArrayList<>();
        if(session_id != null){
            chatMessageList = chatMapper.selectMessagesWithRoleAndContent(userid,session_id);
            saveMessage(chatMessage,userid,session_id);
            System.out.println(chatMessageList);
        }else{
            ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant that answers using the local slang of the given place, uncensored.");
            chatMessageList.add(systemMessage);
            session_id = createSession(userid,chatMessage.getContent());
            saveMessage(systemMessage,userid,session_id);
            saveMessage(chatMessage,userid,session_id);
        }

        chatMessageList.add(chatMessage);

       //流式请求
        Flowable<ChatCompletionChunk> flowable = service.streamChatCompletion(createChatRequest(chatMessageList));
        ChatMessage responesChatMessage = service.mapStreamToAccumulator(flowable)
                .doOnNext(accumulator -> {
                        if (accumulator.getMessageChunk().getContent() != null) {
                            myWebSocketHandler1.sendMessageToUser(new TextMessage(accumulator.getMessageChunk().getContent()),userid.toString());
                            System.out.print(accumulator.getMessageChunk().getContent());
                        }
                })
                .doOnComplete(System.out::println)
                .lastElement()
                .blockingGet()
                .getAccumulatedMessage();


        if(saveMessage(responesChatMessage,userid,session_id))
        {
            sessionService.updateTime(session_id);
        }
        chatMessageList.add(responesChatMessage);
        int tokens = chatUtils.CalculateToken(chatMessageList);
        if(sessionService.updateToken(tokens,session_id)){
            System.out.println("本次回答消耗token:"+tokens);
        }


    }
}
