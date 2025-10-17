// 学生实体类，示例用，描述学生的基本信息。
package com.example.blogsystemdemo.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 学生实体类，示例用，封装学生信息。
 */
@Component
@Data
public class Student {
    @Value("${student.sno}")
    private String sno;
    @Value("${student.sname}")
    private String name;
    @Value("${student.sgender}")
    private String gender;
}
