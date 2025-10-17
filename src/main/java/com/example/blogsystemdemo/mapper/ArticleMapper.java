package com.example.blogsystemdemo.mapper;

/**
 * 文章 Mapper，定义对文章表的数据库操作方法。
 * 文章相关数据库操作的 MyBatis 映射接口。
 */

import com.example.blogsystemdemo.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    public List<Article> getAllArticles(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    //创建文章
    public int createArticle(Article article);

    //修改文章
    public int updateArticle(Article article);

    //删除文章
    public int deleteArticle(Long id);

    //根据id查询文章
    public Article getArticleById(Long id);

    /**
     * 累加文章浏览量（用于Redis同步）
     *
     * @param articleId 文章ID
     * @param increment 增量值
     */
    int incrementViewCountBy(@Param("articleId") Long articleId, @Param("increment") int increment);

    // 获取文章总数
    int getTotalArticles();

    /**
     * 按阅读量降序查询热门文章（分页）
     *
     * @param pageNum  起始页码（从0开始，与getAllArticles保持一致）
     * @param pageSize 每页条数
     * @return 热门文章列表
     */
    List<Article> getHotArticles(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    /**
     * 获取热门文章总数（用于分页计算总页数）
     *
     * @return 文章总数（与getTotalArticles结果一致，可直接复用，但为了接口语义清晰单独定义）
     */
    int getTotalHotArticles();

    /**
     * 新增：更新文章点赞数（+1或-1）
     *
     * @param articleId 文章ID
     * @param num       增量：1=点赞，-1=取消点赞
     */
    void updateLikeCount(@Param("articleId") Long articleId, @Param("num") int num);

    /**
     * 新增：更新文章收藏数（+1或-1）
     *
     * @param articleId 文章ID
     * @param num       增量：1=收藏，-1=取消收藏
     */
    void updateCollectionCount(@Param("articleId") Long articleId, @Param("num") int num);

    // ===== 新增：按作者查询我的文章 =====
    List<Article> getArticlesByAuthor(@Param("authorId") Long authorId,
                                      @Param("pageNum") int pageNum,
                                      @Param("pageSize") int pageSize);

    int countArticlesByAuthor(@Param("authorId") Long authorId);

}