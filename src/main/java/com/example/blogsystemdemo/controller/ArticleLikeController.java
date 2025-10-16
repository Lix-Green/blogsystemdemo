package com.example.blogsystemdemo.controller;

import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.service.ArticleLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article/like")
public class ArticleLikeController {
    @Autowired
    private ArticleLikeService articleLikeService;

    /**
     * 1. 点赞/取消点赞（核心接口）
     * 实际项目中：userId 从登录态解析（如JWT Token），而非前端传参
     */
    @PostMapping("/toggle")
    public Result<Boolean> toggleLike(
            @RequestParam("articleId") Long articleId,
            @RequestHeader(value = "User-Id", required = false) Long headerUserId,
            @RequestParam(value = "userId", required = false) Long paramUserId) {
        Long userId = headerUserId != null ? headerUserId : paramUserId;
        if (userId == null) {
            return Result.error("缺少用户信息，请登录后重试");
        }
        return articleLikeService.toggleLike(articleId, userId);
    }

    /**
     * 2. 查询用户点赞状态（用于前端显示「已点赞/未点赞」按钮）
     */
    @GetMapping("/status")
    public Result<Boolean> getLikeStatus(
            @RequestParam("articleId") Long articleId,
            @RequestHeader(value = "User-Id", required = false) Long headerUserId,
            @RequestParam(value = "userId", required = false) Long paramUserId) {
        Long userId = headerUserId != null ? headerUserId : paramUserId;
        if (userId == null) {
            return Result.success(false);
        }
        return articleLikeService.isLiked(articleId, userId);
    }

    /**
     * 3. 查询文章点赞数（用于文章详情页显示）
     */
    @GetMapping("/count")
    public Result<Integer> getLikeCount(@RequestParam("articleId") Long articleId) {
        return articleLikeService.countLike(articleId);
    }
}
