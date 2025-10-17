// 文章点赞相关数据库操作的 MyBatis 映射接口。
package com.example.blogsystemdemo.mapper;

import com.example.blogsystemdemo.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章点赞 Mapper，定义对文章点赞表的数据库操作方法。
 */
@Mapper
public interface ArticleLikeMapper {
    /**
     * 1. 新增点赞记录
     *
     * @param articleLike 点赞实体
     * @return 影响行数（1=成功，0=失败）
     */
    int insertLike(ArticleLike articleLike);

    /**
     * 2. 删除点赞记录（取消点赞）
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 影响行数
     */
    int deleteLike(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 3. 判断用户是否已点赞某文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 1=已点赞，0=未点赞（若为null则无记录）
     */
    Integer checkLike(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 4. 统计某文章的总点赞数
     *
     * @param articleId 文章ID
     * @return 点赞总数
     */
    Integer countLikeByArticleId(@Param("articleId") Long articleId);
}
