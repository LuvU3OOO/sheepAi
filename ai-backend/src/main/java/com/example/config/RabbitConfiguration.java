package com.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitConfiguration {
    @Bean
    public Queue emailBuilder(){
        return QueueBuilder.durable("mail").build();  // 声明一个持久化队列

    }

}
