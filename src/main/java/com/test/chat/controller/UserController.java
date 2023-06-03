package com.test.chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.test.chat.common.R;
import com.test.chat.domain.User;
import com.test.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private  UserService userService;

    @PostMapping("/login")
    @Transactional
    public R login(HttpServletRequest request,User user){
//        System.out.println(user.getUsername());
        String password = user.getPassword();
//        //加密
//        if(password!=null){
//            password = DigestUtils.md5DigestAsHex(password.getBytes());
//        }
        LambdaQueryWrapper<User> queryWrapper= new LambdaQueryWrapper<>();
//        QueryWrapper这个不能使用::getusername获取属性名
        queryWrapper.eq(User::getUsername, user.getUsername());
        User one = userService.getOne(queryWrapper);
        if(one==null){
            return R.error("账号不存在");
        }
        if(password!=null&&!password.equals(one.getPassword())){
            return R.error("密码错误");
        }
        userService.changeStatus(1,user.getUsername());
        one.setStatus(1);
        one.setPassword("");
        request.getSession().setAttribute("user",one);
        return R.success(one);
    }
    @PostMapping("/getUser")
    public R login(HttpServletRequest request){
//        System.out.println(request.getSession().getAttribute("user"));
        User user = (User) request.getSession().getAttribute("user");

        if(user==null){
            return R.error("不存在登录信息！");
        }
        return R.success(user);
    }
    @PostMapping("/getFriends")
    public R getFriends(String username){
        List<User> friends = userService.getFriends(username);
        return R.success(friends);
    }

    @PostMapping("/searchUser")
    public R searchUser(String nickname,String username){
//        System.out.println("nick:"+nickname);
//        System.out.println("user:"+username);
        List users = userService.getUsers(nickname, username);
        return R.success(users);
    }

    @PostMapping("/setStatus")
    public R setUserStatus(HttpServletRequest request,String status){
        System.out.println(status);
        User user = (User)request.getSession().getAttribute("user");
        try{
            userService.setUserStatus(user.getUsername(),Integer.parseInt(status));
        }catch (Exception e){
            System.out.println("userService错误！");
        }
        return R.success("修改成功");
    }

    @PostMapping("/logout")
    public R logout(HttpServletRequest request,String username){
        userService.changeStatus(0,username);
        request.getSession().removeAttribute("user");
        return R.success("退出成功！");
    }
}
