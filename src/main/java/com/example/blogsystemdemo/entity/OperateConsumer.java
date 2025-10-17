// RabbitMQ 消费者实体类，处理消息队列中的操作消息。
package com.example.blogsystemdemo.entity;


import com.example.blogsystemdemo.constant.RabbitMQConstants;
import com.example.blogsystemdemo.enums.OperationType;
import com.example.blogsystemdemo.mapper.ArticleCollectionMapper;
import com.example.blogsystemdemo.mapper.ArticleLikeMapper;
import com.example.blogsystemdemo.mapper.ArticleMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * RabbitMQ 操作消息消费者，处理点赞、收藏等异步操作。
 */
@Component
@Slf4j
public class OperateConsumer {

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Autowired
    private ArticleCollectionMapper articleCollectionMapper;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 处理点赞/取消点赞消息
     */
    @RabbitListener(queues = RabbitMQConstants.LIKE_QUEUE)
    @Transactional
    public void handleLikeMessage(OperateMessage message, Channel channel, Message mqMessage) throws IOException {
        long deliveryTag = mqMessage.getMessageProperties().getDeliveryTag();

        try {
            // 验证消息类型
            if (message.getType() != OperationType.LIKE_TOGGLE) {
                log.error("收到不符合类型的消息: {}", message);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 执行点赞/取消点赞逻辑
            Long articleId = message.getArticleId();

            Long userId = message.getUserId();
            log.info("收到点赞消息，articleId: {}, userId: {}", message.getArticleId(), message.getUserId());
            Integer isLiked = articleLikeMapper.checkLike(articleId, userId);
            if (isLiked != null && isLiked > 0) {
                // 取消点赞
                int deleteRows = articleLikeMapper.deleteLike(articleId, userId);
                if (deleteRows <= 0) {
                    throw new RuntimeException("取消点赞失败，未找到点赞记录");
                }
                articleMapper.updateLikeCount(articleId, -1);
            } else {
                // 点赞
                ArticleLike articleLike = new ArticleLike(articleId, userId);
                int insertRows = articleLikeMapper.insertLike(articleLike);
                if (insertRows <= 0) {
                    throw new RuntimeException("点赞失败，无法新增点赞记录");
                }
                articleMapper.updateLikeCount(articleId, 1);
            }
            log.info("点赞消息处理完成");
            // 手动确认消息已处理
            channel.basicAck(deliveryTag, false);
            log.info("成功处理点赞消息: articleId={}, userId={}", articleId, userId);

        } catch (Exception e) {
            log.error("处理点赞消息失败: {}", e.getMessage(), e);
            // 拒绝消息并将其放入死信队列
            channel.basicReject(deliveryTag, false);
        }
    }

    /**
     * 处理收藏/取消收藏消息
     */
    @RabbitListener(queues = RabbitMQConstants.COLLECTION_QUEUE)
    @Transactional
    public void handleCollectionMessage(OperateMessage message, Channel channel, Message mqMessage) throws IOException {
        long deliveryTag = mqMessage.getMessageProperties().getDeliveryTag();

        try {
            // 验证消息类型
            if (message.getType() != OperationType.COLLECTION_TOGGLE) {
                log.error("收到不符合类型的消息: {}", message);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 执行收藏/取消收藏逻辑
            Long articleId = message.getArticleId();
            Long userId = message.getUserId();

            Integer isCollected = articleCollectionMapper.checkCollection(articleId, userId);
            if (isCollected != null && isCollected > 0) {
                // 取消收藏
                int deleteRows = articleCollectionMapper.deleteCollection(articleId, userId);
                if (deleteRows <= 0) {
                    throw new RuntimeException("取消收藏失败，未找到收藏记录");
                }
                articleMapper.updateCollectionCount(articleId, -1);
            } else {
                // 收藏
                ArticleCollection collection = new ArticleCollection(articleId, userId);
                int insertRows = articleCollectionMapper.insertCollection(collection);
                if (insertRows <= 0) {
                    throw new RuntimeException("收藏失败，无法新增收藏记录");
                }
                articleMapper.updateCollectionCount(articleId, 1);
            }

            // 手动确认消息已处理
            channel.basicAck(deliveryTag, false);
            log.info("成功处理收藏消息: articleId={}, userId={}", articleId, userId);

        } catch (Exception e) {
            log.error("处理收藏消息失败: {}", e.getMessage(), e);
            // 拒绝消息并将其放入死信队列
            channel.basicReject(deliveryTag, false);
        }
    }
}
