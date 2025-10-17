// 留言相关数据库操作的 MyBatis 映射接口。
package com.example.blogsystemdemo.mapper;

import com.example.blogsystemdemo.entity.Message;

import java.util.List;

/**
 * 留言 Mapper，定义对留言表的数据库操作方法。
 */
public interface MessageMapper {
    List<Message> getAllMessages();

    int addMessage(Message message);
}
