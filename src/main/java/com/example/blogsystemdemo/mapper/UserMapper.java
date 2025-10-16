package com.example.blogsystemdemo.mapper;

import com.example.blogsystemdemo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    //个人信息显示
    User getUserById(@Param("id") Long id);

    //登录
    User getUserByUsername(@Param("username") String username);

    //注册
    int createUser(User user);

    //更新个人信息
    int updateUser(User user);
}
