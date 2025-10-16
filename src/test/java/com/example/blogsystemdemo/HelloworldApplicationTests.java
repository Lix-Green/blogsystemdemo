package com.example.blogsystemdemo;

import com.example.blogsystemdemo.entity.Article;
import com.example.blogsystemdemo.entity.Comment;
import com.example.blogsystemdemo.entity.Student;
import com.example.blogsystemdemo.entity.User;
import com.example.blogsystemdemo.mapper.ArticleMapper;
import com.example.blogsystemdemo.mapper.CommentMapper;
import com.example.blogsystemdemo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class blogsystemdemoApplicationTests {
    @Autowired
    private Student student;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Test
    void contextLoads() {
        System.out.println(student);
    }

    @Test
    public void update() {
        Article article = new Article();
        article.setTitle("测试文章标题11");
        article.setContent("测试文章内容11");
        article.setSummary("测试文章摘要11");
        article.setAuthorId(1l);
        article.setCoverImage("https://picsum.photos/800/400");
        article.setStatus(1);
        article.setId(33l);
        int result = articleMapper.updateArticle(article);
        System.out.println("修改返回值：" + result);
    }

    @Test
    public void create() {
        Article article = new Article();
        article.setTitle("测试文章标题");
        article.setContent("测试文章内容");
        article.setSummary("测试文章摘要");
        article.setAuthorId(1l);
        article.setCoverImage("https://picsum.photos/800/400");
        article.setStatus(1);
        int result = articleMapper.createArticle(article);
        System.out.println(result);
    }

    @Test
    public void delete() {
        articleMapper.deleteArticle(33l);
    }

    @Test
    public void getinfo() {
        User user = userMapper.getUserById(1l);
        System.out.println(user);
    }

    @Test
    public void findUserByUsername() {
        User user = userMapper.getUserByUsername("admin");
        System.out.println(user);
    }

    @Test
    public void createUser() {
        User user = new User();
        user.setUsername("test11");
        user.setPassword("123456");
        user.setEmail("test@test.com");
        user.setAvatar("https://picsum.photos/800/400");
        user.setStatus(1);
        int result = userMapper.createUser(user);
        System.out.println(result);
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setNickname("王五");
        user.setEmail("test11@test.com");
        user.setAvatar("https://picsum.photos/800/400");
        user.setStatus(1);
        user.setId(15l);
        int result = userMapper.updateUser(user);
        System.out.println(result);
    }

    @Test
    public void createcomment() {
        Comment comment = new Comment();
        comment.setArticleId(2l);
        comment.setContent("测试评论内容");
        comment.setParentId(0l);
        comment.setUserId(1l);
        commentMapper.createComment(comment);
    }

    @Test
    public void deletecomment() {
        commentMapper.deleteCommentById(38l);
    }

    @Test
    public void getcomment() {
        Comment comment = commentMapper.getCommentById(36l);
        System.out.println(comment);
    }

    public void getcomments() {
        List<Comment> comments = commentMapper.listCommentsByArticleId(22l);
        System.out.println(comments);
    }
}
