package com.example.blogsystemdemo.entity;

import java.time.LocalDateTime;

/**
 * 留言实体类，描述留言板的留言信息。
 * 对应数据库中的留言表，封装留言相关字段。
 */
public class Message {
    private Long id;
    private String content;
    private Long userId;
    private LocalDateTime createTime;
    private String nickname;

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
