import request from './index';

// 创建文章
export const createArticle = (data) => {
    return request({
        url: '/articles',
        method: 'POST',
        data
    });
};

// 获取文章列表（分页）
export const getArticleList = (params) => {
    return request({
        url: '/articles',
        method: 'GET',
        params
    });
};

// 获取当前用户的文章列表（需登录，使用请求头 User-Id）
export const getMyArticles = (params) => {
    return request({
        url: '/articles/my',
        method: 'GET',
        params
    });
};

// 获取文章详情
export const getArticleDetail = (id) => {
    return request({
        url: `/articles/${id}`,
        method: 'GET'
    });
};

// 更新文章
export const updateArticle = (id, data) => {
    return request({
        url: `/articles/${id}`,
        method: 'PUT',
        data
    });
};
// 删除文章
export const deleteArticle = (id) => {
    return request({
        url: `/articles/${id}`,
        method: 'DELETE'
    });
};

// 获取最新文章（首页用）
export const getLatestArticles = () => {
    return getArticleList({pageNum: 1, pageSize: 10});
};

// 获取热门文章（使用request代替原生axios，与其他接口风格统一）
export const getHotArticles = (pageNum = 1, pageSize = 5) => {
    return request({
        url: '/articles/hot',
        method: 'GET',
        params: {
            pageNum,
            pageSize  // 前端热门文章若只显示5条，传pageSize=5
        }
    });
};

// 1. 文章点赞/取消点赞（异步提交到RabbitMQ）
export const toggleArticleLike = (articleId) => {
    return request({
        url: '/article/like/toggle',
        method: 'post',
        params: {articleId}
    });
};
// 2. 查询文章点赞状态（用户是否已点赞）
export const getArticleLikeStatus = (articleId) => {
    return request({
        url: '/article/like/status',
        method: 'get',
        params: {articleId}
    });
};


// 4. 文章收藏/取消收藏（异步提交到RabbitMQ）
export const toggleArticleCollection = (articleId) => {
    return request({
        url: '/article/collection/toggle',
        method: 'post',
        params: {articleId}
    });
};
// 4. 查询文章收藏状态（用户是否已收藏）
export const getArticleCollectionStatus = (articleId) => {
    return request({
        url: '/article/collection/status',
        method: 'get',
        params: {articleId}
    });
};

// 5. 查询当前用户收藏的文章ID列表
export const getMyCollectedArticleIds = () => {
    return request({
        url: '/article/collection/user/list',
        method: 'get'
    });
};
