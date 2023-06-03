package com.test.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.chat.domain.User;
import com.test.chat.mapper.UserMapper;
import com.test.chat.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper usermapper;

    @Override
    public List<User> getFriends(String username) {
        List<User> users = usermapper.queryFriends(username);
        return users;
    }

    @Override
    public void changeStatus(int status,String username) {
        usermapper.setStatus(status,username);
    }

    @Override
    public List getUsers(String nickname, String username) {
        List list=null;
        if (username!=null){
            list = usermapper.getUserByUsername(username);
        }else{
            list = usermapper.getUserByNickname(nickname);
        }
        return list;
    }

    @Override
    public void setUserStatus(String username, int status) {
        usermapper.setStatus(status,username);
    }

    public int getUserStatus(String username){
        int status = usermapper.getUserStatus(username);
        return status;
    }

}
