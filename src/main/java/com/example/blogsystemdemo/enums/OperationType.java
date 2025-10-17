/**
 * 操作类型枚举，描述 RabbitMQ 消息中的操作类型，如点赞、收藏。
 */
package com.example.blogsystemdemo.enums;

public enum OperationType {
    LIKE_TOGGLE,        // 点赞/取消点赞
    COLLECTION_TOGGLE   // 收藏/取消收藏
}
