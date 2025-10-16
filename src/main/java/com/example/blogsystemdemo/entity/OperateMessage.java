package com.example.blogsystemdemo.entity;

import com.example.blogsystemdemo.enums.OperationType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作消息
 */
@Data
public class OperateMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;         // 用户ID
    private Long articleId;      // 文章ID
    private OperationType type;  // 操作类型
    private LocalDateTime sendTime; // 发送时间
}
