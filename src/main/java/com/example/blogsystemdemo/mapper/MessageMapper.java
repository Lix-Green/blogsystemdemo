package com.example.blogsystemdemo.mapper;

import com.example.blogsystemdemo.entity.Message;

import java.util.List;

public interface MessageMapper {
    List<Message> getAllMessages();

    int addMessage(Message message);
}
