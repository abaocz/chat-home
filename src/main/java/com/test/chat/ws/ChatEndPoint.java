package com.test.chat.ws;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.chat.domain.Message;
import com.test.chat.domain.SaveMessage;
import com.test.chat.domain.User;
import com.test.chat.service.MessageService;
import com.test.chat.service.UserService;
import com.test.chat.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@Component
@ServerEndpoint(value = "/chat",configurator = GetHttpSessionConfigurator.class)
public class ChatEndPoint {

    //用来存储每一个客户端对象对应的chatEndPoint对象
    private static Map<String,ChatEndPoint> maps=new HashMap<>();
    //声明session对象，通过该对象可以发送消息给指定的用户
    private Session session;
    //声明一个httpSession对象，我们之前在httpSession中存储了登录的用户信息
    private HttpSession httpSession;

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService){
        ChatEndPoint.userService=userService;
    }

    private static MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService){
        ChatEndPoint.messageService=messageService;
    }

    //连接建立时
    @OnOpen
    public void open(Session session, EndpointConfig config){
        try {
            this.session = session;
            //获取httpSession对象
            this.httpSession = (HttpSession) config.getUserProperties().get("httpSession");

            User user = (User) httpSession.getAttribute("user");
            if (user!=null){
                userService.setUserStatus(user.getUsername(),1);
            }
            //将当前对象存到容器中
            maps.put(user.getUsername(), this);
            List<User> friends = userService.getFriends(user.getUsername());

            List list = new ArrayList();
            for (User u : friends) {
                list.add(u.getUsername());
            }
            //将当前你在线的信息发送给你的好友
            //1.获取消息
            String message = MessageUtil.getMessage(true, null, user.getNickname() + "(" + user.getUsername() + ")");
            //2.调用方法进行系统消息的推送,发送给当前用户的朋友
            sendMessages(getNames(list), message);
        }catch (Exception e){
            System.out.println("httpService错误");
        }
    }
    //接收到客户端发送的信息时
    @OnMessage
    public void message(String message,Session session){
        //获取当前登录的用户
        User user = (User)httpSession.getAttribute("user");
        if (user==null){
            return;
        }
        //把json字符串转为json对象
        ObjectMapper mapper=new ObjectMapper();
        Message msg=null;

        if(message.contains("消息请求")){
            String to = message.split(":")[1];
            System.out.println(to);
            //查询数据库是否有离线信息存在，如果存在就遍历发送，然后删除
            LambdaQueryWrapper<SaveMessage> queryWrapper= new LambdaQueryWrapper<>();
            //QueryWrapper这个不能使用::getusername获取属性名
//            queryWrapper.eq(SaveMessage::getRecipient,user.getUsername())
//                    .or().eq(SaveMessage::getSender,user.getUsername());

            queryWrapper.and(wrapper -> wrapper.eq(SaveMessage::getRecipient,
                    user.getUsername()).or().eq(SaveMessage::getSender
                    ,user.getUsername())).and(wrapper -> wrapper
                    .eq(SaveMessage::getRecipient,to)
                    .or().eq(SaveMessage::getSender,to));

            // 根据 id 字段降序排序 // 限制只查询最后 20 条数
            queryWrapper.last("ORDER BY message_id DESC LIMIT 20");
            List<SaveMessage> messages = messageService.list(queryWrapper);
            for (int i=messages.size()-1;i>=0;i--) {
                sendOneMessage(user.getUsername(),messages.get(i).getContent());
            }
            return;
        }

        try {
            msg = mapper.readValue(message, Message.class);
            //获取信息发给的用户
            String toName = msg.getToName();
            //获取消息
            String data = msg.getMessage();
            String resultMessage = MessageUtil.getMessage(false, user.getUsername(), data);
            System.out.println(resultMessage);
            System.out.println(toName);
            //发送信息给指定的用户
            //判断发送信息的用户是否在线
//            System.out.println(userService.getUserStatus(toName));
            //想了想还是都存吧
//            if(userService.getUserStatus(toName)==0){
                //如果不在线。
//                sender:userName,recipient:toName,content:resultMessage
            SaveMessage saveMessage =new SaveMessage();
            saveMessage.setSender(user.getUsername());
            saveMessage.setRecipient(toName);
            saveMessage.setContent(resultMessage);
            messageService.save(saveMessage);
//                return;
//            }
//            if(userService.getUserStatus(toName)==0){
//                return;
//            }
            maps.get(toName).session.getBasicRemote().sendText(resultMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //连接关闭时
    @OnClose
    public void close(Session session){
        if (userService==null||httpSession==null){
            return;
        }
        User user = (User) httpSession.getAttribute("user");
        if (user!=null)
            userService.setUserStatus(user.getUsername(),0);
        try {
            //将当前对象存到容器中
            List<User> friends = userService.getFriends(user.getUsername());

            List list = new ArrayList();
            for (User u : friends) {
                list.add(u.getUsername());
            }
            //将当前你离线的信息发送给你的好友
            //1.获取消息，由于设计问题吗，没有设置离线提示，我这边使用fromName=1表示返回时离线，null为上线
            String message = MessageUtil.getMessage(true, "1", user.getNickname() + "(" + user.getUsername() + ")");
            //2.调用方法进行系统消息的推送,发送给当前用户的朋友
            sendMessages(getNames(list), message);
        }catch (Exception e){
            System.out.println("httpService_close错误");
        }
    }

    public Set getNames(List list){
        Set<String> keySet = maps.keySet();
        Set set=new HashSet();
        for (String s:keySet) {
            if(list.contains(s)){
                set.add(s);
            }
        }
        return set;
    }

    public void sendMessages(Set<String> set,String message){
        try {
            for (String name:set) {
                ChatEndPoint chatEndPoint=maps.get(name);
                chatEndPoint.session.getBasicRemote().sendText(message);
            }
        }catch (Exception e){
//            e.printStackTrace();
            System.out.println("sendMessage错误");
        }
    }

    public void sendOneMessage(String name,String message){
        try {
            ChatEndPoint chatEndPoint=maps.get(name);
            chatEndPoint.session.getBasicRemote().sendText(message);
        }catch (Exception e){
//            e.printStackTrace();
            System.out.println("sendMessage错误");
        }
    }
}
