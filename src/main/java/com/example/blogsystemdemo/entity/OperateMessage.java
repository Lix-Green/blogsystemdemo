// RabbitMQ 操作消息实体类，封装异步操作的消息内容。
package com.example.blogsystemdemo.entity;

import com.example.blogsystemdemo.enums.OperationType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * RabbitMQ 操作消息实体类，描述点赞、收藏等操作的消息结构。
 */
@Data
public class OperateMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;         // 用户ID
    private Long articleId;      // 文章ID
    private OperationType type;  // 操作类型
    private LocalDateTime sendTime; // 发送时间
}
