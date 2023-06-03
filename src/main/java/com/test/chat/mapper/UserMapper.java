package com.test.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.chat.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    public List queryFriends(@Param("username") String username);

    void setStatus(@Param("status") int status,@Param("username") String username);

    List getUserByNickname(@Param("nickname") String nickname);

    List getUserByUsername(@Param("username") String username);

    int getUserStatus(@Param("username") String username);
}
