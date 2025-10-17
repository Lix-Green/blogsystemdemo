// 评论数据传输对象（DTO），用于传递评论相关数据。
package com.example.blogsystemdemo.dto;

import lombok.Data;

/**
 * 评论DTO，封装评论内容、作者等信息。
 */
@Data
public class CommentDTO {
    private Long articleId;
    private String content;
    private int parentId;
}
