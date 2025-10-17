/*
 * 留言板相关接口封装，负责与后端进行留言的获取、发布等 HTTP 请求。
 */

import request from './index';

/**
 * 获取留言列表
 * @returns {Promise}
 */
export const getMessages = () => request({url: '/messages', method: 'GET'});

/**
 * 新增留言
 * @param {Object} data 留言内容
 * @returns {Promise}
 */
export const addMessage = (data) => request({url: '/messages', method: 'POST', data});
