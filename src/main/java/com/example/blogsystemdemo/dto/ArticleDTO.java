// 文章数据传输对象（DTO），用于前后端或各层之间传递文章相关数据。
package com.example.blogsystemdemo.dto;

import lombok.Data;

/**
 * 文章DTO，封装文章的标题、内容、摘要等信息。
 */
@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Integer status;
}
