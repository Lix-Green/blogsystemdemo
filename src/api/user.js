/*
 * 用户相关接口封装，负责与后端进行用户的登录、注册、信息获取与修改等 HTTP 请求。
 */
import request from './index';

/**
 * 用户登录
 * @param {Object} data 登录信息
 * @returns {Promise}
 */
export const login = (data) => {
    return request({
        url: '/users/login',
        method: 'POST',
        data
    });
};

/**
 * 用户注册
 * @param {Object} data 注册信息
 * @returns {Promise}
 */
export const register = (data) => {
    return request({
        url: '/users/register',  // 注意后端注册接口是/users/register
        method: 'POST',
        data
    });
};

/**
 * 更新用户信息
 * @param {number|string} id 用户ID
 * @param {Object} data 新的用户信息
 * @returns {Promise}
 */
export const updateUser = (id, data) => {
    return request({
        url: `/users/${id}`,
        method: 'PUT',
        data
    });
};

/**
 * 删除用户
 * @param {number|string} id 用户ID
 * @returns {Promise}
 */
export const deleteUser = (id) => {
    return request({
        url: `/users/${id}`,
        method: 'DELETE'
    });
};
