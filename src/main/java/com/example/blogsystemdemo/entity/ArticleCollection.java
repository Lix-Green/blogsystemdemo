package com.example.blogsystemdemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleCollection {
    private Long id;          // 主键ID
    private Long articleId;   // 文章ID
    private Long userId;      // 用户ID
    private LocalDateTime createTime;  // 收藏时间

    public ArticleCollection(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
    }
}
