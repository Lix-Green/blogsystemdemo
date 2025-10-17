/*
 * API 统一出口文件，集中导出各模块接口，便于统一管理和调用。
 */
import axios from 'axios';
import {ElMessage} from 'element-plus';

// 创建axios实例
const request = axios.create({
    baseURL: 'http://localhost:9090/api',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器：自动添加用户ID到请求头
request.interceptors.request.use(
    (config) => {
        // 从localStorage获取用户信息
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        if (userInfo && userInfo.id) {
            config.headers['User-Id'] = userInfo.id;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// 响应拦截器：统一处理错误提示
request.interceptors.response.use(
    (response) => response.data,
    (error) => {
        ElMessage.error(error.response?.data?.message || '请求失败');
        return Promise.reject(error);
    }
);

export default request;
