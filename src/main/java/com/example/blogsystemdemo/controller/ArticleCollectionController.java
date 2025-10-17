// 处理文章收藏相关请求的控制器。
package com.example.blogsystemdemo.controller;

import com.example.blogsystemdemo.constant.RabbitMQConstants;
import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.entity.OperateMessage;
import com.example.blogsystemdemo.enums.OperationType;
import com.example.blogsystemdemo.service.ArticleCollectionService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章收藏控制器，负责处理文章收藏相关的 HTTP 请求。
 */
@RestController
@RequestMapping("/article/collection")
public class ArticleCollectionController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ArticleCollectionService articleCollectionService;

    /**
     * 收藏/取消收藏（发送消息到RabbitMQ）
     */
    @PostMapping("/toggle")
    public Result<Boolean> toggleCollection(
            @RequestParam("articleId") Long articleId,
            @RequestHeader(value = "User-Id", required = false) Long userId) {
        // 参数校验
        if (articleId == null) {
            return Result.error("文章ID无效");
        }
        if (userId == null || userId <= 0) {
            return Result.error("未登录，无法收藏/取消收藏");
        }

        // 构建消息
        OperateMessage message = new OperateMessage();
        message.setUserId(userId);
        message.setArticleId(articleId);
        message.setType(OperationType.COLLECTION_TOGGLE);
        message.setSendTime(LocalDateTime.now());

        try {
            // 发送消息到RabbitMQ
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.OPERATE_EXCHANGE,
                    RabbitMQConstants.COLLECTION_ROUTING_KEY,
                    message
            );
            return Result.success(true, "操作已提交，请稍后查看结果");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 查询用户收藏状态
     */
    @GetMapping("/status")
    public Result<Boolean> getCollectionStatus(
            @RequestParam("articleId") Long articleId,
            @RequestHeader(value = "User-Id", required = false) Long userId) {
        if (userId == null || userId <= 0) {
            // 未登录默认未收藏，避免400
            return Result.success(false, "未登录，未收藏");
        }
        return articleCollectionService.isCollected(articleId, userId);
    }

    /**
     * 查询文章收藏数（保持不变）
     */
    @GetMapping("/count")
    public Result<Integer> getCollectionCount(@RequestParam("articleId") Long articleId) {
        return articleCollectionService.countCollection(articleId);
    }

    /**
     * 查询用户收藏列表（未登录返回空列表，避免400）
     */
    @GetMapping("/user/list")
    public Result<List<Long>> getUserCollectedArticles(
            @RequestHeader(value = "User-Id", required = false) Long userId) {
        if (userId == null || userId <= 0) {
            return Result.success(java.util.Collections.emptyList(), "未登录，返回空收藏");
        }
        return articleCollectionService.listCollectedArticleIds(userId);
    }
}
