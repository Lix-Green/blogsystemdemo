package com.example.blogsystemdemo.service.impl;

import com.example.blogsystemdemo.dto.CommentDTO;
import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.entity.Comment;
import com.example.blogsystemdemo.entity.User;
import com.example.blogsystemdemo.mapper.CommentMapper;
import com.example.blogsystemdemo.mapper.UserMapper;
import com.example.blogsystemdemo.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<?> addComment(CommentDTO commentDTO, Long userId) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        comment.setUserId(userId);
        int rows = commentMapper.createComment(comment);
        if (rows > 0) {
            // 更新文章的评论数量
            List<Comment> comments = commentMapper.listCommentsByArticleId(comment.getArticleId());
            commentMapper.updateArticleCommentCount(comment.getArticleId(), comments.size());
            Long commentId = comment.getId();
            System.out.println("***********" + commentId);
            // 返回创建的评论，包含用户信息
            Comment newComment = commentMapper.getCommentById(comment.getId());
            User user = userMapper.getUserById(userId);
            newComment.setUser(user);

            return Result.success(comment);
        }
        return Result.error("创建评论失败");
    }

    @Override
    public Result<Boolean> deleteCommentById(Long commentId, Long userId) {
        Comment comment = commentMapper.getCommentById(commentId);
        if (comment == null) {
            return Result.error("评论不存在");
        }

        // 验证权限，只有评论者才能删除
        if (!comment.getUserId().equals(userId)) {
            return Result.error("没有权限删除此评论");
        }

        int rows = commentMapper.deleteCommentById(commentId);
        if (rows > 0) {
            // 更新文章的评论数量
            List<Comment> comments = commentMapper.listCommentsByArticleId(comment.getArticleId());
            commentMapper.updateArticleCommentCount(comment.getArticleId(), comments.size());

            return Result.success(true);
        }
        return Result.error("删除评论失败");
    }

    @Override
    public Result<?> getCommentsByArticleId(Long articleId) {
        List<Comment> comments = commentMapper.listCommentsByArticleId(articleId);

        // 填充用户信息
        for (Comment comment : comments) {
            User user = userMapper.getUserById(comment.getUserId());
            comment.setUser(user);
        }

        return Result.success(comments);
    }
}
