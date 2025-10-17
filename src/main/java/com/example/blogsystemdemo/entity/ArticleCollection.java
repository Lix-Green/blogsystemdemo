// 文章收藏实体类，对应数据库中的收藏表，封装收藏相关字段。
package com.example.blogsystemdemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章收藏实体类，描述用户收藏文章的关系。
 */
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
