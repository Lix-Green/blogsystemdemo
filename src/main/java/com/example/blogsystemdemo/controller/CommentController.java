package com.example.blogsystemdemo.controller;

import com.example.blogsystemdemo.dto.CommentDTO;
import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 创建评论
     */
    @PostMapping
    public Result<?> createComment(@RequestBody CommentDTO commentDTO,
                                   @RequestHeader(value = "User-Id", required = false, defaultValue = "1") Long userId) {
        return commentService.addComment(commentDTO, userId);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteComment(@PathVariable Long id,
                                         @RequestHeader(value = "User-Id", required = false, defaultValue = "1") Long userId) {
        return commentService.deleteCommentById(id, userId);
    }

    /**
     * 获取文章的评论列表
     */
    @GetMapping("/article/{articleId}")
    public Result<?> getCommentsByArticleId(@PathVariable Long articleId) {
        return commentService.getCommentsByArticleId(articleId);
    }
}
