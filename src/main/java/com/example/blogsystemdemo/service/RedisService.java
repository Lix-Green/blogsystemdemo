package com.example.blogsystemdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    // 关键：@Qualifier名称必须与 RedisConfig 中 @Bean(name = "redisTemplate") 一致
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 增加文章浏览量（Redis 计数）
     */
    public void incrementArticleView(Long articleId) {
        // 若 redisTemplate 仍为 null，会抛空指针；若不为 null，说明注入成功
        if (redisTemplate == null) {
            System.err.println("RedisTemplate 注入失败！");
            return;
        }
        // 验证连接地址
        LettuceConnectionFactory factory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        System.out.println("Redis 连接地址：" + factory.getHostName() + ":" + factory.getPort());
        String key = "article:view:" + articleId;
        redisTemplate.opsForValue().increment(key);
        // 设置过期时间为7天，避免内存泄漏
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * 获取文章浏览量
     */
    public Long getArticleViewCount(Long articleId) {
        String key = "article:view:" + articleId;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.valueOf(count.toString()) : 0L;
    }

    /**
     * 增加文章点赞数（Redis 计数）
     */
    public void incrementArticleLike(Long articleId) {
        String key = "article:like:" + articleId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * 减少文章点赞数（Redis 计数）
     */
    public void decrementArticleLike(Long articleId) {
        String key = "article:like:" + articleId;
        redisTemplate.opsForValue().decrement(key);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * 获取文章点赞数
     */
    public Long getArticleLikeCount(Long articleId) {
        String key = "article:like:" + articleId;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.valueOf(count.toString()) : 0L;
    }

    /**
     * 增加文章收藏数（Redis 计数）
     */
    public void incrementArticleCollection(Long articleId) {
        String key = "article:collection:" + articleId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * 减少文章收藏数（Redis 计数）
     */
    public void decrementArticleCollection(Long articleId) {
        String key = "article:collection:" + articleId;
        redisTemplate.opsForValue().decrement(key);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * 获取文章收藏数
     */
    public Long getArticleCollectionCount(Long articleId) {
        String key = "article:collection:" + articleId;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.valueOf(count.toString()) : 0L;
    }

    /**
     * 缓存热门文章列表
     */
    public void cacheHotArticles(String key, Object data) {
        redisTemplate.opsForValue().set(key, data, 10, TimeUnit.MINUTES);
    }

    /**
     * 获取缓存的热门文章列表
     */
    public Object getCachedHotArticles(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     */
    public void deleteCache(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     */
    public void deleteCachePattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}

