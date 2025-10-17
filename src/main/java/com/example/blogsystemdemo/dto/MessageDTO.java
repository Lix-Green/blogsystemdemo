/**
 * 留言数据传输对象（DTO），用于传递留言相关数据。
 */
package com.example.blogsystemdemo.dto;

import java.time.LocalDateTime;

/**
 * 留言DTO，封装留言内容、用户等信息。
 */
public class MessageDTO {
    private Long id;
    private String content;
    private Long userId;
    private LocalDateTime createTime;

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
}
