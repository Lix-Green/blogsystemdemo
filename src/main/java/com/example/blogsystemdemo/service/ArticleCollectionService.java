package com.example.blogsystemdemo.service;

import com.example.blogsystemdemo.dto.Result;

import java.util.List;

public interface ArticleCollectionService {
    /**
     * 收藏/取消收藏
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return Result<Boolean>：data为当前收藏状态（true=已收藏，false=未收藏）
     */
    Result<Boolean> toggleCollection(Long articleId, Long userId);

    /**
     * 判断用户是否已收藏某文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return Result<Boolean>：data为收藏状态
     */
    Result<Boolean> isCollected(Long articleId, Long userId);

    /**
     * 统计某文章的收藏数
     *
     * @param articleId 文章ID
     * @return Result<Integer>：data为收藏总数
     */
    Result<Integer> countCollection(Long articleId);

    /**
     * 查询用户收藏的所有文章ID
     *
     * @param userId 用户ID
     * @return Result<List<Long>>：data为收藏文章ID列表
     */
    Result<List<Long>> listCollectedArticleIds(Long userId);
}
