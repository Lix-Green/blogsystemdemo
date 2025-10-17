package com.example.blogsystemdemo.service.impl;

/**
 * 用户 Service 实现类，实现用户相关的业务逻辑。
 */

import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.dto.UserDTO;
import com.example.blogsystemdemo.entity.User;
import com.example.blogsystemdemo.mapper.UserMapper;
import com.example.blogsystemdemo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<User> getUserById(Long id) {
        User user = userMapper.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");

        }
        return Result.success(user);

    }

    @Override
    public Result<User> getUserByUsername(String username) {
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @Override
    public Result<User> updateUser(User user) {
        // 检查用户是否存在
        User existingUser = userMapper.getUserById(user.getId());
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        // ���止更新用户名（如果需要可以修改此逻辑）
        user.setUsername(existingUser.getUsername());

        int rows = userMapper.updateUser(user);
        if (rows > 0) {
            return Result.success(userMapper.getUserById(user.getId()));
        }
        return Result.error("更新用户信息失败");
    }

    @Override
    public Result<User> login(UserDTO userDTO) {
        // 参数校验
        if (userDTO == null || userDTO.getUsername() == null || userDTO.getPassword() == null) {
            return Result.error("用户名或密码不能为空");
        }

        // 根据用户名查询用户
        User user = userMapper.getUserByUsername(userDTO.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证密码
        if (!user.getPassword().equals(userDTO.getPassword())) {
            return Result.error("密码错误");
        }

        // 登录成功，可以在这里处理会话或生成令牌
        // 出于安全考虑，返回用户信息时不返回密码
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<User> register(UserDTO userDTO) {
        // 1. 参数校验
        if (userDTO == null) {
            return Result.error("注册信息不能为空");
        }
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        // 2. 检查用户名是否已存在
        User existingUser = userMapper.getUserByUsername(userDTO.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }
        // 3. DTO转换为实体对象
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setStatus(1); // 假设1表示正常状态
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        // 4. 保存用户
        int rows = userMapper.createUser(user);
        if (rows > 0) {
            // 出于安全考虑，返回用户信息时不返回密码
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("注册失败");
    }
}
