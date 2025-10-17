// 评论实体类，对应数据库中的评论表，封装评论相关字段。
package com.example.blogsystemdemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类，描述文章评论的基本信息。
 */
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
