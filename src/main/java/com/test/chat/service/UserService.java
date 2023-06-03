package com.test.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.chat.domain.User;

import java.util.List;

public interface UserService extends IService<User> {
    List<User> getFriends(String username);

    void changeStatus(int status,String username);

    List getUsers(String nickname, String username);

    void setUserStatus(String username, int status);

    int getUserStatus(String username);
}
