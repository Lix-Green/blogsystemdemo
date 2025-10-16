package com.example.blogsystemdemo.controller;

import com.example.blogsystemdemo.dto.ArticleDTO;
import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    // 模拟当前登录用户ID，实际项目中应从认证信息中获取
    private Long currentUserId = 1L;

    @GetMapping()
    public Result<?> listArticles(@RequestParam(defaultValue = "1") int pageNum,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        return articleService.listArticles(pageNum, pageSize);
    }

    /**
     * 创建文章
     */
    @PostMapping
    public Result<Long> createArticle(@RequestBody ArticleDTO articleDTO,
                                      @RequestHeader(value = "User-Id", required = true) Long userId) {
        return articleService.createArticle(articleDTO, userId);
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateArticle(@PathVariable Long id,
                                         @RequestBody ArticleDTO articleDTO,
                                         @RequestHeader(value = "User-Id", required = true) Long userId) {
        return articleService.updateArticle(id, articleDTO, userId);
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteArticle(@PathVariable Long id,
                                         @RequestHeader(value = "User-Id", required = true) Long userId) {
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
     *
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10，可根据前端需求调整，如热门文章只显示5条则设为5）
     * @return 热门文章分页结果
     */
    @GetMapping("/hot")
    public Result<?> getHotArticles(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        // 直接调用业务层方法，返回结果
        return articleService.getHotArticles(pageNum, pageSize);
    }

    /**
     * 获取当前登录用户的文章列表（需登录）
     */
    @GetMapping("/my")
    public Result<?> getMyArticles(
            @RequestHeader(value = "User-Id", required = true) Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return articleService.getMyArticles(userId, pageNum, pageSize);
    }
}
