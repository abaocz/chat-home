package com.test.chat.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.chat.domain.ResultMessage;

//用来封装消息的工具类
public class MessageUtil {

    public static String getMessage(boolean isSystemMessage,String fromName,Object message){
        ResultMessage result=new ResultMessage();
        result.setIsSystem(isSystemMessage);
        result.setMessage(message);
        if(fromName!=null){
            result.setFromName(fromName);
        }
        ObjectMapper mapper=new ObjectMapper();
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
