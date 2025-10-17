// 处理留言相关请求的控制器。
package com.example.blogsystemdemo.controller;

import com.example.blogsystemdemo.dto.Result;
import com.example.blogsystemdemo.entity.Message;
import com.example.blogsystemdemo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言控制器，负责处理留言板相关的 HTTP 请求。
 */
@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping
    public Result<List<Message>> getMessages() {
        List<Message> messages = messageService.getAllMessages();
        return Result.success(messages);
    }

    @PostMapping
    public Result<?> addMessage(@RequestBody Message message, @RequestHeader("User-Id") Long userId) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return Result.error("content不能为空");
        }
        message.setUserId(userId);
        message.setCreateTime(java.time.LocalDateTime.now());
        int result = messageService.addMessage(message);
        return result > 0 ? Result.success(null, "留言成功") : Result.error("留言失败");
    }
}
