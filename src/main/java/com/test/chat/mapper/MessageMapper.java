package com.test.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.chat.domain.SaveMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<SaveMessage> {
}
