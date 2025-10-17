// 用户实体类，对应数据库中的用户表，封装用户相关字段。
package com.example.blogsystemdemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类，描述用户的基本信息和数据库字段。
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
