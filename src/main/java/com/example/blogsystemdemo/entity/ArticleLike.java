// 文章点赞实体类，对应数据库中的点赞表，封装点赞相关字段。
package com.example.blogsystemdemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章点赞实体类，描述用户点赞文章的关系。
 */
@Data
public class ArticleLike {
    private Long id;          // 主键ID
    private Long articleId;   // 文章ID（关联article表）
    private Long userId;      // 用户ID（关联user表）
    private LocalDateTime createTime;  // 点赞时间（数据库自动填充）

    // 带参构造（用于快速创建对象）
    public ArticleLike(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
    }
}
