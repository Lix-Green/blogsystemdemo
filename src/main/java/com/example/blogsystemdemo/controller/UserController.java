package com.example.blogsystemdemo.controller;

import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.dto.UserDTO;
import com.example.blogsystemdemo.entity.User;
import com.example.blogsystemdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public Result<?> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }


    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<?> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        // 确保路径中的ID与请求体中的ID一致
        user.setId(id);
        return userService.updateUser(user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody UserDTO userAuthDTO) {
        return userService.login(userAuthDTO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody UserDTO userAuthDTO) {
        return userService.register(userAuthDTO);
    }


}
