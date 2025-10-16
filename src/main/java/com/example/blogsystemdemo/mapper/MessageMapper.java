package com.example.blogsystemdemo.mapper;

import com.example.blogsystemdemo.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<Message> getAllMessages();

    int addMessage(Message message);
}
