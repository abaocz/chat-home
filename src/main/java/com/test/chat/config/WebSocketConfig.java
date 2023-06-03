package com.test.chat.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


@Configuration
public class WebSocketConfig {

    @Bean
    //自动注册使用了@ServerEndPoint注解的bean
    public ServerEndpointExporter getEndPoint(){
        return new ServerEndpointExporter();
    }
}
