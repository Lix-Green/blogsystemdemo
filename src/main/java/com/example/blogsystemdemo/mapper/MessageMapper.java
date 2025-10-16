package com.example.blogsystemdemo.mapper;

import com.example.blogsystemdemo.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {
    @Select("SELECT * FROM message ORDER BY create_time DESC")
    List<Message> getAllMessages();

    int addMessage(Message message);
}
