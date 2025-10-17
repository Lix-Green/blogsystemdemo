/*
 * 评论相关接口封装，负责与后端进行评论的获取、发布、删除等 HTTP 请求。
 */
import request from './index';

/**
 * 创建评论
 * @param {Object} data 评论数据
 * @returns {Promise}
 */
export const createComment = (data) => {
    return request({
        url: '/comments',
        method: 'POST',
        data
    });
};

/**
 * 获取指定文章的评论列表
 * @param {number|string} articleId 文章ID
 * @returns {Promise}
 */
export const getArticleComments = (articleId) => {
    return request({
        url: `/comments/article/${articleId}`,
        method: 'GET'
    });
};

/**
 * 删除评论
 * @param {number|string} id 评论ID
 * @returns {Promise}
 */
export const deleteComment = (id) => {
    return request({
        url: `/comments/${id}`,
        method: 'DELETE'
    });
};
