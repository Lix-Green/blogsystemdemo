import request from './index';
// 创建评论
export const createComment = (data) => {
    return request({
        url: '/comments',
        method: 'POST',
        data
    });
};
// 获取文章的评论
export const getArticleComments = (articleId) => {
    return request({
        url: `/comments/article/${articleId}`,
        method: 'GET'
    });
};
// 删除评论
export const deleteComment = (id) => {
    return request({
        url: `/comments/${id}`,
        method: 'DELETE'
    });
};
