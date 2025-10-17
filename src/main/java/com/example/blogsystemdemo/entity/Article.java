package com.example.blogsystemdemo.entity;

/**
 * 文章实体类，描述文章的基本信息和数据库字段。
 * 对应数据库中的文章表，封装文章的所有字段。
 */

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Article {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Long authorId;
    private Integer viewCount;
    private Integer commentCount;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private User author;
    // 新增：点赞数、收藏数（与数据库字段对应）
    private Integer likeCount;     // 文章点赞数
    private Integer collectionCount; // 文章收藏数
}
