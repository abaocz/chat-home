package com.test.chat.ws;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;


public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        try{
            sec.getUserProperties().put("httpSession",httpSession);
        }catch (Exception e){
            System.out.println("GetHttpSessionConfigurator错误");
        }

    }
}
