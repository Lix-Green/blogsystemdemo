package com.example.blogsystemdemo.service;

import com.example.blogsystemdemo.dto.ArticleDTO;
import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.entity.Article;

public interface ArticleService {
    // 分页获取文章列表
    Result<?> listArticles(int pageNum, int pageSize);

    // 创建文章
    Result<Long> createArticle(ArticleDTO articleDTO, Long userId);

    // 更新文章
    Result<Boolean> updateArticle(Long id, ArticleDTO articleDTO, Long userId);

    // 根据ID删除文章
    Result<Boolean> deleteArticleById(Long id, Long userId);

    public Result<Article> getArticleById(Long id);

    /**
     * 查询热门文章（分页）
     *
     * @param pageNum  起始页码（从1开始，前端传入常规页码，后端转换为0开始的索引）
     * @param pageSize 每页条数
     * @return 包含热门文章列表、分页信息的Result
     */
    Result<?> getHotArticles(int pageNum, int pageSize);

    public Result<?> getMyArticles(Long userId, int pageNum, int pageSize);

}
