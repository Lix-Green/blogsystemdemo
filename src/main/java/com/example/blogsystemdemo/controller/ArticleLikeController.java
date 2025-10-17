// 处理文章点赞相关请求的控制器。
package com.example.blogsystemdemo.controller;

import com.example.blogsystemdemo.constant.RabbitMQConstants;
import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.entity.OperateMessage;
import com.example.blogsystemdemo.enums.OperationType;
import com.example.blogsystemdemo.service.ArticleLikeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 文章点赞控制器，负责处理文章点赞相关的 HTTP 请求。
 */
@RestController
@RequestMapping("/article/like")
public class ArticleLikeController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ArticleLikeService articleLikeService;

    /**
     * 点赞/取消点赞（发送消息到RabbitMQ）
     */
    @PostMapping("/toggle")
    public Result<Boolean> toggleLike(
            @RequestParam("articleId") Long articleId,
            @RequestHeader(value = "User-Id", required = false) Long userId) {
        // 参数校验
        if (articleId == null) {
            return Result.error("文章ID无效");
        }
        if (userId == null || userId <= 0) {
            return Result.error("未登录，无法点赞/取消点赞");
        }

        // 构建消息
        OperateMessage message = new OperateMessage();
        message.setUserId(userId);
        message.setArticleId(articleId);
        message.setType(OperationType.LIKE_TOGGLE);
        message.setSendTime(LocalDateTime.now());

        try {
            // 发送消息到RabbitMQ
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.OPERATE_EXCHANGE,
                    RabbitMQConstants.LIKE_ROUTING_KEY,
                    message
            );
            return Result.success(true, "操作已提交，请稍后查看结果");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 查询用户点赞状态
     */
    @GetMapping("/status")
    public Result<Boolean> getLikeStatus(
            @RequestParam("articleId") Long articleId,
            @RequestHeader(value = "User-Id", required = false) Long userId) {
        if (userId == null || userId <= 0) {
            // 未登录默认未点赞，避免400
            return Result.success(false, "未登录，未点赞");
        }
        return articleLikeService.isLiked(articleId, userId);
    }

    /**
     * 查询文章点赞数
     */
    @GetMapping("/count")
    public Result<Integer> getLikeCount(@RequestParam("articleId") Long articleId) {
        return articleLikeService.countLike(articleId);
    }
}
