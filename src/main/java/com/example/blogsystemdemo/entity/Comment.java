package com.example.blogsystemdemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long articleId;
    private Long userId;
    private String content;
    private Long parentId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private transient User user;//评论会显示昵称
}
