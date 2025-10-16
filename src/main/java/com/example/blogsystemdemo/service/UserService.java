package com.example.blogsystemdemo.service;

import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.dto.UserDTO;
import com.example.blogsystemdemo.entity.User;

public interface UserService {
    Result<User> getUserById(Long id);

    Result<User> getUserByUsername(String username);

    Result<User> updateUser(User user);

    // 添加登录方法
    Result<User> login(UserDTO userDTO);

    // 添加注册方法
    Result<User> register(UserDTO userDTO);
}

