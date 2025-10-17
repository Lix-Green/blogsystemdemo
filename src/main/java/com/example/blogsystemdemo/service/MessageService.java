package com.example.blogsystemdemo.service;

/**
 * 留言 Service，定义留言相关的业务方法。
 */

import com.example.blogsystemdemo.entity.Message;
import com.example.blogsystemdemo.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    public List<Message> getAllMessages() {
        return messageMapper.getAllMessages();
    }

    public int addMessage(Message message) {
        return messageMapper.addMessage(message);
    }
}
