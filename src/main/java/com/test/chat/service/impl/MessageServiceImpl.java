package com.test.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.chat.domain.SaveMessage;
import com.test.chat.mapper.MessageMapper;
import com.test.chat.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, SaveMessage> implements MessageService {
}
