package com.yifu.emos.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther YIFU GAO
 * @date 2023/01/11/20:38
 * File Info:
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory getFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.43.82");
        factory.setPort(5672);
        return factory;
    }
}
