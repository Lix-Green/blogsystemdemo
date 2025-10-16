package com.example.blogsystemdemo.service.impl;

import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.entity.ArticleCollection;
import com.example.blogsystemdemo.mapper.ArticleCollectionMapper;
import com.example.blogsystemdemo.mapper.ArticleMapper;
import com.example.blogsystemdemo.service.ArticleCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleCollectionServiceImpl implements ArticleCollectionService {
    @Autowired
    private ArticleCollectionMapper articleCollectionMapper;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 收藏/取消收藏：事务保证
     */
    @Override
    public Result<Boolean> toggleCollection(Long articleId, Long userId) {
        // 1. 参数校验
        if (articleId == null || userId == null) {
            return Result.error("文章ID或用户ID不能为空");
        }

        // 2. 判断用户是否已收藏
        Integer isCollected = articleCollectionMapper.checkCollection(articleId, userId);
        try {
            if (isCollected != null && isCollected > 0) {
                // 2.1 已收藏：取消收藏（删除记录 + 收藏数-1）
                int deleteRows = articleCollectionMapper.deleteCollection(articleId, userId);
                if (deleteRows <= 0) {
                    return Result.error("取消收藏失败，未找到收藏记录");
                }
                articleMapper.updateCollectionCount(articleId, -1);
                return Result.success(false); // 当前状态：未收藏
            } else {
                // 2.2 未收藏：收藏（新增记录 + 收藏数+1）
                ArticleCollection collection = new ArticleCollection(articleId, userId);
                int insertRows = articleCollectionMapper.insertCollection(collection);
                if (insertRows <= 0) {
                    return Result.error("收藏失败，无法新增收藏记录");
                }
                articleMapper.updateCollectionCount(articleId, 1);
                return Result.success(true); // 当前状态：已收藏
            }
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 判断用户是否已收藏
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
     * 统计文章收藏数
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
     * 查询用户收藏的文章ID列表
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
