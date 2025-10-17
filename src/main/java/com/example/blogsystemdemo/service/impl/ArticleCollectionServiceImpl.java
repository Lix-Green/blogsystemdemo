// 文章收藏相关业务实现类，实现收藏相关的服务方法。
package com.example.blogsystemdemo.service.impl;

import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.mapper.ArticleCollectionMapper;
import com.example.blogsystemdemo.service.ArticleCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章收藏 Service 实现类，实现收藏相关的业务逻辑。
 */
@Service
public class ArticleCollectionServiceImpl implements ArticleCollectionService {
    @Autowired
    private ArticleCollectionMapper articleCollectionMapper;

    /**
     * 判断用户是否已收藏（保持不变）
     */
    @Override
    public Result<Boolean> isCollected(Long articleId, Long userId) {
        if (articleId == null || userId == null) {
            return Result.error("文章ID或用户ID不能为空");
        }
        Integer count = articleCollectionMapper.checkCollection(articleId, userId);
        boolean status = count != null && count > 0;
        return Result.success(status);
    }

    /**
     * 统计文章收藏数（保持不变）
     */
    @Override
    public Result<Integer> countCollection(Long articleId) {
        if (articleId == null) {
            return Result.error("文章ID不能为空");
        }
        Integer count = articleCollectionMapper.countCollectionByArticleId(articleId);
        return Result.success(count == null ? 0 : count);
    }

    /**
     * 查询用户收藏的文章ID列表（保持不变）
     */
    @Override
    public Result<List<Long>> listCollectedArticleIds(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        List<Long> articleIds = articleCollectionMapper.listCollectedArticleIds(userId);
        return Result.success(articleIds);
    }
}
