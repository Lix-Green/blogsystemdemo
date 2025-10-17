package com.example.blogsystemdemo.service.impl;

/**
 * 文章点赞 Service 实现类，实现点赞相关的业务逻辑。
 */

import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.entity.ArticleLike;
import com.example.blogsystemdemo.mapper.ArticleLikeMapper;
import com.example.blogsystemdemo.mapper.ArticleMapper;
import com.example.blogsystemdemo.service.ArticleLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleLikeServiceImpl implements ArticleLikeService {
    @Autowired
    private ArticleLikeMapper articleLikeMapper;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 点赞/取消点赞：事务保证原子性
     */
    @Override
    public Result<Boolean> toggleLike(Long articleId, Long userId) {
        // 1. 参数校验
        if (articleId == null || userId == null) {
            return Result.error("文章ID或用户ID不能为空");
        }

        // 2. 判断用户是否已点赞
        Integer isLiked = articleLikeMapper.checkLike(articleId, userId);
        try {
            if (isLiked != null && isLiked > 0) {
                // 2.1 已点赞：取消点赞（删除记录 + 点赞数-1）
                int deleteRows = articleLikeMapper.deleteLike(articleId, userId);
                if (deleteRows <= 0) {
                    return Result.error("取消点赞失败��未找到点赞记录");
                }
                articleMapper.updateLikeCount(articleId, -1);
                return Result.success(false); // 返回当前状态：未点赞
            } else {
                // 2.2 未点赞：点赞（新增记录 + 点赞数+1）
                ArticleLike articleLike = new ArticleLike(articleId, userId);
                int insertRows = articleLikeMapper.insertLike(articleLike);
                if (insertRows <= 0) {
                    return Result.error("点赞失败，无法新增点赞记录");
                }
                articleMapper.updateLikeCount(articleId, 1);
                return Result.success(true); // 返回当前状态：已点赞
            }
        } catch (Exception e) {
            // 捕获异常，返回错误信息
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 判断用户是否已点赞
     */
    @Override
    public Result<Boolean> isLiked(Long articleId, Long userId) {
        if (articleId == null || userId == null) {
            return Result.error("文章ID或用户ID不能为空");
        }
        Integer count = articleLikeMapper.checkLike(articleId, userId);
        boolean status = count != null && count > 0;
        return Result.success(status);
    }

    /**
     * 统计文章点赞数
     */
    @Override
    public Result<Integer> countLike(Long articleId) {
        if (articleId == null) {
            return Result.error("文章ID不能为空");
        }
        Integer count = articleLikeMapper.countLikeByArticleId(articleId);
        return Result.success(count == null ? 0 : count);
    }
}
