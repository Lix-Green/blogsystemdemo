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
            @RequestHeader(value = "User-Id", required = true) Long userId) {
        // 参数校验
        if (articleId == null || userId == null || userId <= 0) {
            return Result.error("文章ID或用户ID无效");
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
     * 查询用户点赞状态（保持不变）
     */
    @GetMapping("/status")
    public Result<Boolean> getLikeStatus(
            @RequestParam("articleId") Long articleId,
            @RequestHeader(value = "User-Id", required = true) Long userId) {
        return articleLikeService.isLiked(articleId, userId);
    }

    /**
     * 查询文章点赞数（保持不变）
     */
    @GetMapping("/count")
    public Result<Integer> getLikeCount(@RequestParam("articleId") Long articleId) {
        return articleLikeService.countLike(articleId);
    }
}
