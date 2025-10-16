package com.example.blogsystemdemo.service.impl;

import com.example.blogsystemdemo.dto.ArticleDTO;
import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.entity.Article;
import com.example.blogsystemdemo.entity.User;
import com.example.blogsystemdemo.mapper.ArticleMapper;
import com.example.blogsystemdemo.mapper.UserMapper;
import com.example.blogsystemdemo.service.ArticleService;
import com.example.blogsystemdemo.service.RedisService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public Result<Article> getArticleById(Long id) {
        Article article = articleMapper.getArticleById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        // 使用 Redis 增加浏览量
        redisService.incrementArticleView(id);
        // 获取Redis中的实时浏览量并更新到返回对象中
        Long redisViewCount = redisService.getArticleViewCount(id);
        // 修复：数据库中已经保存的是历史累计值，Redis中是未同步的增量
        article.setViewCount(article.getViewCount() + redisViewCount.intValue());
        // 获取作者信息
        User author = userMapper.getUserById(article.getAuthorId());
        article.setAuthor(author);
        return Result.success(article);
    }

    @Override
    public Result<?> listArticles(int pageNum, int pageSize) {
        // 计算分页起始位置
        int start = (pageNum - 1) * pageSize;

        List<Article> articles = articleMapper.getAllArticles(start, pageSize);
        int total = articleMapper.getTotalArticles();

        // 填充作者信息
        for (Article article : articles) {
            User author = userMapper.getUserById(article.getAuthorId());
            article.setAuthor(author);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("articles", articles);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));

        return Result.success(result);
    }

    @Override
    public Result<Long> createArticle(ArticleDTO articleDTO, Long userId) {
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        article.setAuthorId(userId);
        int rows = articleMapper.createArticle(article);
        if (rows > 0) {
            return Result.success(article.getId());
        }
        return Result.error("创建文章失败");
    }

    @Override
    public Result<Boolean> updateArticle(Long id, ArticleDTO articleDTO, Long userId) {

        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        article.setId(id);
        // 验证权限，只有作者才能修改
//        if (!article.getAuthorId().equals(userId)) {
//            return Result.error("没有权限修改此文章");
//        }
        int rows = articleMapper.updateArticle(article);
        return Result.success(rows > 0);
    }

    @Override
    public Result<Boolean> deleteArticleById(Long id, Long userId) {
        int rows = articleMapper.deleteArticle(id);
        return Result.success(rows > 0);
    }

    /**
     * 实现热门文章查询（复用最新文章的分页、作者信息填充逻辑）
     */
    @Override
    public Result<?> getHotArticles(int pageNum, int pageSize) {
        // 1. 分页参数转换：前端传入pageNum从1开始，转换为SQL的0开始索引
        int start = (pageNum - 1) * pageSize;

        // 2. 查询热门文章列表（按阅读量降序）
        List<Article> hotArticles = articleMapper.getHotArticles(start, pageSize);
        // 3. 查询文章总数（用于计算总页数）
        int total = articleMapper.getTotalHotArticles();

        // 4. 填充作者信息（与最新文章逻辑一致，确保返回数据包含作者昵称、头像等）
        for (Article article : hotArticles) {
            User author = userMapper.getUserById(article.getAuthorId());
            article.setAuthor(author);
        }
        // 5. 封装分页结果（字段与getAllArticles一致，降低前端适配成本）
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("articles", hotArticles);       // 热门文章列表
        resultMap.put("total", total);                // 总文章数
        resultMap.put("pageNum", pageNum);            // 当前页码
        resultMap.put("pageSize", pageSize);          // 每页条数
        resultMap.put("totalPages", (int) Math.ceil((double) total / pageSize));  // 总页数

        // 6. 返回成功结果
        return Result.success(resultMap);
    }

    @Override
    public Result<?> getMyArticles(Long userId, int pageNum, int pageSize) {
        int start = (pageNum - 1) * pageSize;

        List<Article> articles = articleMapper.getArticlesByAuthor(userId, start, pageSize);
        int total = articleMapper.countArticlesByAuthor(userId);

        for (Article article : articles) {
            User author = userMapper.getUserById(article.getAuthorId());
            article.setAuthor(author);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("articles", articles);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));

        return Result.success(result);
    }
}
