package com.example.blogsystemdemo.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String username;     // 用户名
    private String password;     // 密码
    private String nickname;     // 昵称（注册时使用）
    private String email;        // 邮箱（注册时使用）
}
