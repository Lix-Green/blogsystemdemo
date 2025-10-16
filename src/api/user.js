import request from './index';
// 用户登录
export const login = (data) => {
    return request({
        url: '/users/login',
        method: 'POST',
        data
    });
};
// 用户注册
export const register = (data) => {
    return request({
        url: '/users/register',  // 注意后端注册接口是/users/register
        method: 'POST',
        data
    });
};
// 更新用户信息
export const updateUser = (id, data) => {
    return request({
        url: `/users/${id}`,
        method: 'PUT',
        data
    });
};

// 删除用户
export const deleteUser = (id) => {
    return request({
        url: `/users/${id}`,
        method: 'DELETE'
    });
};

