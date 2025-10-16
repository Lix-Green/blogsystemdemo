package com.example.blogsystemdemo.controller;

import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.service.ArticleCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article/collection")
public class ArticleCollectionController {
    @Autowired
    private ArticleCollectionService articleCollectionService;

    /**
     * 1. 收藏/取消收藏（核心接口）
     */
    @PostMapping("/toggle")
    public Result<Boolean> toggleCollection(
            @RequestParam("articleId") Long articleId,
            @RequestHeader(value = "User-Id", required = false) Long headerUserId,
            @RequestParam(value = "userId", required = false) Long paramUserId) {
        Long userId = headerUserId != null ? headerUserId : paramUserId;
        if (userId == null) {
            return Result.error("缺少用户信息，请登录后重试");
        }
        return articleCollectionService.toggleCollection(articleId, userId);
    }

    /**
     * 2. 查询用户收藏状态
     */
    @GetMapping("/status")
    public Result<Boolean> getCollectionStatus(
            @RequestParam("articleId") Long articleId,
            @RequestHeader(value = "User-Id", required = false) Long headerUserId,
            @RequestParam(value = "userId", required = false) Long paramUserId) {
        Long userId = headerUserId != null ? headerUserId : paramUserId;
        if (userId == null) {
            return Result.success(false);
        }
        return articleCollectionService.isCollected(articleId, userId);
    }

    /**
     * 3. 查询文章收藏数
     */
    @GetMapping("/count")
    public Result<Integer> getCollectionCount(@RequestParam("articleId") Long articleId) {
        return articleCollectionService.countCollection(articleId);
    }

    /**
     * 4. 查询用户收藏列表（用于「我的收藏」页面）
     */
    @GetMapping("/user/list")
    public Result<List<Long>> getUserCollectedArticles(
            @RequestHeader(value = "User-Id", required = false) Long headerUserId,
            @RequestParam(value = "userId", required = false) Long paramUserId) {
        Long userId = headerUserId != null ? headerUserId : paramUserId;
        if (userId == null) {
            return Result.error("缺少用户信息，请登录后重试");
        }
        return articleCollectionService.listCollectedArticleIds(userId);
    }
}
