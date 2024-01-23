package com.example.websocket;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.dto.Account;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;


public class MyWebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {


    @Resource
    AccountService service;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            String userid = servletRequest.getServletRequest().getParameter("userid");
            attributes.put("userid",userid);
            return service.isAccountExist(userid);

        }

       return true;
    }


}
