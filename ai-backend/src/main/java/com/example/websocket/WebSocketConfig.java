package com.example.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //配置handler,拦截器和跨域
        registry.addHandler(myHandler(), "/ws2").addInterceptors(myWebSocketHandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new MyWebSocketHandler1();
    }

    @Bean
    public  MyWebSocketHandshakeInterceptor myWebSocketHandshakeInterceptor(){
        return  new MyWebSocketHandshakeInterceptor();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);  //文本消息最大缓存
        container.setMaxBinaryMessageBufferSize(8192);  //二进制消息大战缓存
        container.setMaxSessionIdleTimeout(10L * 60 * 1000); // 最大闲置时间，10分钟没动自动关闭连接
        container.setAsyncSendTimeout(10L * 1000); //异步发送超时时间
        return container;
    }

}
