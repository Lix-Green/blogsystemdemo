package com.example.blogsystemdemo.mapper;

import com.example.blogsystemdemo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 创建评论
    int createComment(Comment comment);

    // 根据ID删除评论
    int deleteCommentById(Long id);

    // 根据文章ID查询评论
    List<Comment> listCommentsByArticleId(@Param("articleId") Long articleId);

    // 根据评论ID查询评论
    Comment getCommentById(Long id);

    // 更新文章的评论数量
    int updateArticleCommentCount(@Param("articleId") Long articleId, @Param("count") int count);
}
