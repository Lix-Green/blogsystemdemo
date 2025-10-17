/**
 * 评论 Service，定义评论相关的业务方法。
 */
package com.example.blogsystemdemo.service;

import com.example.blogsystemdemo.dto.CommentDTO;
import com.example.blogsystemdemo.dto.Result;

public interface CommentService {
    public Result<?> addComment(CommentDTO commentDTO, Long userId);

    Result<Boolean> deleteCommentById(Long commentId, Long userId);

    // 获取文章的评论列表
    Result<?> getCommentsByArticleId(Long articleId);
}
