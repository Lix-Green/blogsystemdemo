// 用户数据传输对象（DTO），用于传递用户相关数据。
package com.example.blogsystemdemo.dto;

import lombok.Data;

/**
 * 用户DTO，封装用户登录、注册等信息。
 */
@Data
public class UserDTO {
    private String username;     // 用户名
    private String password;     // 密码
    private String nickname;     // 昵称（注册时使用）
    private String email;        // 邮箱（注册时使用）
}
