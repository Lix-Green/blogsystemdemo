package com.example.blogsystemdemo.service;

import com.example.blogsystemdemo.dto.Result;

public interface ArticleLikeService {
    /**
     * 点赞/取消点赞（toggle切换）
     *
     * @param articleId 文章ID
     * @param userId    用户ID（从登录态解析，此处简化传参）
     * @return Result<Boolean>：data为当前点赞状态（true=已点赞，false=未点赞）
     */
    Result<Boolean> toggleLike(Long articleId, Long userId);

    /**
     * 判断用户是否已点赞某文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return Result<Boolean>：data为点赞状态
     */
    Result<Boolean> isLiked(Long articleId, Long userId);

    /**
     * 统计某文章的点赞数
     *
     * @param articleId 文章ID
     * @return Result<Integer>：data为点赞总数
     */
    Result<Integer> countLike(Long articleId);
}
