package com.example.blogsystemdemo.task;

import com.example.blogsystemdemo.mapper.ArticleMapper;
import com.example.blogsystemdemo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisSyncTask {

    @Autowired
    private RedisService redisService;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 每5分钟同步一次浏览量到数据库（修复后：累加而非覆盖）
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void syncViewCounts() {
        try {
            // 获取所有浏览量 key
            Set<String> viewKeys = redisTemplate.keys("article:view:*");
            if (viewKeys != null && !viewKeys.isEmpty()) {
                for (String key : viewKeys) {
                    Long articleId = Long.valueOf(key.substring("article:view:".length()));
                    Long increment = redisService.getArticleViewCount(articleId);
                    if (increment > 0) {
                        // 修复：将Redis中的增量累加到数据库中
                        articleMapper.incrementViewCountBy(articleId, increment.intValue());
                        // 清空 Redis 中的计数，下次重新累计
                        redisTemplate.delete(key);
                    }
                }
                System.out.println("同步浏览量完成，处理了 " + viewKeys.size() + " 篇文章");
            }
        } catch (Exception e) {
            System.err.println("同步浏览量失败: " + e.getMessage());
        }
    }

    /**
     * 每10分钟同步一次点赞数到数据库
     */
    @Scheduled(fixedRate = 600000) // 10分钟 = 600000毫秒
    public void syncLikeCounts() {
        // 现有逻辑正确，点赞数本身就是增量更新
        try {
            Set<String> likeKeys = redisTemplate.keys("article:like:*");
            if (likeKeys != null && !likeKeys.isEmpty()) {
                for (String key : likeKeys) {
                    Long articleId = Long.valueOf(key.substring("article:like:".length()));
                    Long likeCount = redisService.getArticleLikeCount(articleId);
                    if (likeCount != 0) {
                        articleMapper.updateLikeCount(articleId, likeCount.intValue());
                        redisTemplate.delete(key);
                    }
                }
                System.out.println("同步点赞数完成，处理了 " + likeKeys.size() + " 篇文章");
            }
        } catch (Exception e) {
            System.err.println("同步点赞数失败: " + e.getMessage());
        }
    }

    /**
     * 每10分钟同步一次收藏数到数据库
     */
    @Scheduled(fixedRate = 600000) // 10分钟 = 600000毫秒
    public void syncCollectionCounts() {
        // 现有逻辑正确，收藏数本身就是增量更新
        try {
            Set<String> collectionKeys = redisTemplate.keys("article:collection:*");
            if (collectionKeys != null && !collectionKeys.isEmpty()) {
                for (String key : collectionKeys) {
                    Long articleId = Long.valueOf(key.substring("article:collection:".length()));
                    Long collectionCount = redisService.getArticleCollectionCount(articleId);
                    if (collectionCount != 0) {
                        articleMapper.updateCollectionCount(articleId, collectionCount.intValue());
                        redisTemplate.delete(key);
                    }
                }
                System.out.println("同步收藏数完成，处理了 " + collectionKeys.size() + " 篇文章");
            }
        } catch (Exception e) {
            System.err.println("同步收藏数失败: " + e.getMessage());
        }
    }
}
