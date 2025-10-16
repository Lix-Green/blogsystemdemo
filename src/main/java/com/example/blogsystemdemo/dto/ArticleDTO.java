package com.example.blogsystemdemo.dto;

import lombok.Data;

@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Integer status;
}
