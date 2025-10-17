// RabbitMQ 相关常量定义类，统一管理交换机、队列、路由键等常量。
package com.example.blogsystemdemo.constant;

/**
 * RabbitMQ 常量类，定义交换机、队列、路由键等常量，便于统一管理和引用。
 */
public class RabbitMQConstants {
    // 交换机名称
    public static final String OPERATE_EXCHANGE = "blog.operate.exchange";

    // 队列名称
    public static final String LIKE_QUEUE = "blog.operate.like.queue";
    public static final String COLLECTION_QUEUE = "blog.operate.collection.queue";

    // 死信交换机和队列
    public static final String DEAD_EXCHANGE = "blog.dead.exchange";
    public static final String LIKE_DEAD_QUEUE = "blog.dead.like.queue";
    public static final String COLLECTION_DEAD_QUEUE = "blog.dead.collection.queue";

    // 路由键
    public static final String LIKE_ROUTING_KEY = "operate.like";
    public static final String COLLECTION_ROUTING_KEY = "operate.collection";
    public static final String LIKE_DEAD_ROUTING_KEY = "dead.like";
    public static final String COLLECTION_DEAD_ROUTING_KEY = "dead.collection";
}
