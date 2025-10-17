// 处理文章相关请求的控制器。
package com.example.blogsystemdemo.controller;

import com.example.blogsystemdemo.dto.ArticleDTO;
import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * 文章控制器，负责处理文章的增删改查等 HTTP 请求。
 */
@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping()
    public Result<?> listArticles(@RequestParam(defaultValue = "1") int pageNum,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        return articleService.listArticles(pageNum, pageSize);
    }

    /**
     * 创建文章（需登录）
     */
    @PostMapping
    public Result<Long> createArticle(@RequestBody ArticleDTO articleDTO,
                                      @RequestHeader(value = "User-Id", required = false) Long userId) {
        if (userId == null || userId <= 0) {
            return Result.error("未登录，无法创建文章");
        }
        return articleService.createArticle(articleDTO, userId);
    }

    /**
     * 更新文章（需登录）
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateArticle(@PathVariable Long id,
                                         @RequestBody ArticleDTO articleDTO,
                                         @RequestHeader(value = "User-Id", required = false) Long userId) {
        if (userId == null || userId <= 0) {
            return Result.error("未登录，无法修改文章");
        }
        return articleService.updateArticle(id, articleDTO, userId);
    }

    /**
     * 删除文章（需登录）
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteArticle(@PathVariable Long id,
                                         @RequestHeader(value = "User-Id", required = false) Long userId) {
        if (userId == null || userId <= 0) {
            return Result.error("未登录，无法删除文章");
        }
        return articleService.deleteArticleById(id, userId);
    }

    /**
     * 获取文章详情（公开接口）
     */
    @GetMapping("/{id}")
    public Result<?> getArticleDetail(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    /**
     * 热门文章查询接口（公开接口，无需登录）
     */
    @GetMapping("/hot")
    public Result<?> getHotArticles(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return articleService.getHotArticles(pageNum, pageSize);
    }

    /**
     * 获取当前登录用户的文章列表（未登录返回空列表）
     */
    @GetMapping("/my")
    public Result<?> getMyArticles(
            @RequestHeader(value = "User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        if (userId == null || userId <= 0) {
            return Result.success(Collections.emptyList(), "未登录，无个人文章");
        }
        return articleService.getMyArticles(userId, pageNum, pageSize);
    }
}
