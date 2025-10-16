package com.example.blogsystemdemo.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
