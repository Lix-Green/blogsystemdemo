// 用户相关数据库操作的 MyBatis 映射接口。
package com.example.blogsystemdemo.mapper;

import com.example.blogsystemdemo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper，定义对用户表的数据库操作方法。
 */
@Mapper
public interface UserMapper {
    //个人信息显示
    User getUserById(@Param("id") Long id);

    //登录
    User getUserByUsername(@Param("username") String username);

    //注册
    int createUser(User user);

    //更新个人信息
    int updateUser(User user);
}
