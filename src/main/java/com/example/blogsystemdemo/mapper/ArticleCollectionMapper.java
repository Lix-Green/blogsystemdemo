// 文章收藏相关数据库操作的 MyBatis 映射接口。
package com.example.blogsystemdemo.mapper;

import com.example.blogsystemdemo.entity.ArticleCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章收藏 Mapper，定义对文章收藏表的数据库操作方法。
 */
@Mapper
public interface ArticleCollectionMapper {
    /**
     * 1. 新增收藏记录
     *
     * @param articleCollection 收藏实体
     * @return 影响行数
     */
    int insertCollection(ArticleCollection articleCollection);

    /**
     * 2. 删除收藏记录（取消收藏）
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 影响行数
     */
    int deleteCollection(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 3. 判断用户是否已收藏某文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 1=已收藏，0=未收藏
     */
    Integer checkCollection(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 4. 统计某文章的总收藏数
     *
     * @param articleId 文章ID
     * @return 收藏总数
     */
    Integer countCollectionByArticleId(@Param("articleId") Long articleId);

    /**
     * 5. 查询用户收藏的所有文章ID（扩展功能）
     *
     * @param userId 用户ID
     * @return 文章ID列表
     */
    List<Long> listCollectedArticleIds(@Param("userId") Long userId);
}
