// RabbitMQ 相关配置类，负责声明交换机、队列、绑定关系等。
package com.example.blogsystemdemo.config;

import com.example.blogsystemdemo.constant.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类，声明交换机、队列、绑定等。
 */
@Configuration
public class RabbitMQConfig {

    // 声明操作交换机
    @Bean
    public TopicExchange operateExchange() {
        return ExchangeBuilder.topicExchange(RabbitMQConstants.OPERATE_EXCHANGE)
                .durable(true)
                .build();
    }

    // 声明死信交换机
    @Bean
    public TopicExchange deadExchange() {
        return ExchangeBuilder.topicExchange(RabbitMQConstants.DEAD_EXCHANGE)
                .durable(true)
                .build();
    }

    // 声明点赞队列
    @Bean
    public Queue likeQueue() {
        return QueueBuilder.durable(RabbitMQConstants.LIKE_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.LIKE_DEAD_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // 消息过期时间60秒
                .build();
    }

    // 声明收藏队列
    @Bean
    public Queue collectionQueue() {
        return QueueBuilder.durable(RabbitMQConstants.COLLECTION_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.COLLECTION_DEAD_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // 消息过期时间60秒
                .build();
    }

    // 声明点赞死信队列
    @Bean
    public Queue likeDeadQueue() {
        return QueueBuilder.durable(RabbitMQConstants.LIKE_DEAD_QUEUE)
                .build();
    }

    // 声明收藏死信队列
    @Bean
    public Queue collectionDeadQueue() {
        return QueueBuilder.durable(RabbitMQConstants.COLLECTION_DEAD_QUEUE)
                .build();
    }

    // 绑定点赞队列到操作交换机
    @Bean
    public Binding likeBinding(Queue likeQueue, TopicExchange operateExchange) {
        return BindingBuilder.bind(likeQueue)
                .to(operateExchange)
                .with(RabbitMQConstants.LIKE_ROUTING_KEY);
    }

    // 绑定收藏队列到操作交换机
    @Bean
    public Binding collectionBinding(Queue collectionQueue, TopicExchange operateExchange) {
        return BindingBuilder.bind(collectionQueue)
                .to(operateExchange)
                .with(RabbitMQConstants.COLLECTION_ROUTING_KEY);
    }

    // 绑定点赞死信队列到死信交换机
    @Bean
    public Binding likeDeadBinding(Queue likeDeadQueue, TopicExchange deadExchange) {
        return BindingBuilder.bind(likeDeadQueue)
                .to(deadExchange)
                .with(RabbitMQConstants.LIKE_DEAD_ROUTING_KEY);
    }

    // 绑定收藏死信队列到死信交换机
    @Bean
    public Binding collectionDeadBinding(Queue collectionDeadQueue, TopicExchange deadExchange) {
        return BindingBuilder.bind(collectionDeadQueue)
                .to(deadExchange)
                .with(RabbitMQConstants.COLLECTION_DEAD_ROUTING_KEY);
    }
}