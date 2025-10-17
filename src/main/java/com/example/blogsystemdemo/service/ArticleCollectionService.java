// 文章收藏相关业务接口，定义收藏相关的服务方法。
package com.example.blogsystemdemo.service;

import com.example.blogsystemdemo.dto.Result;

import java.util.List;

/**
 * 文章收藏 Service，定义收藏相关的业务方法。
 */
public interface ArticleCollectionService {


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
