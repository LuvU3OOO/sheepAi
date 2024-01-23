package com.example.websocket;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Slf4j
public class MyWebSocketHandler1 implements WebSocketHandler {


    //保存用户会话信息，用于服务端群发
    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 建立连接时保存用户ID和对应的WebSocketSession
        String userid = (String) session.getAttributes().get("userid");
        userSessions.put(userid,session);
        log.info("用户{}已连接",userid);

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 处理WebSocket消息
        // 其他处理逻辑...

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // 处理传输错误
        // 其他处理逻辑...
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String closeId = (String) session.getAttributes().get("userid");
        userSessions.remove(closeId);
        log.info("用户{}断开连接",closeId);
    }



    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    public void sendMessageToUser(WebSocketMessage<?> message, String userid) {

        WebSocketSession session = userSessions.get(userid);
        // 根据用户ID获取对应的WebSocketSession
        if (session != null && session.isOpen()) {
            try {
                // 发送消息到指定用户
                session.sendMessage(message);
            } catch (IOException e) {
                log.error("Error sending message to user: {}", e);
            }
        }else{
            System.out.println("会话为空");
        }
    }

}
