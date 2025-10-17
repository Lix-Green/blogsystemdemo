// 用户状态管理（Pinia），负责登录、注册、信息存储与提示
import {defineStore} from 'pinia';
import {login, register, updateUser} from '../api/user';
import {ElMessage} from 'element-plus';

/**
 * useUserStore 用户数据仓库
 * state: userInfo 用户信息，isLogin 登录状态
 * actions: login 登录、register 注册、updateUser 更新信息
 */
export const useUserStore = defineStore('user', {
    state: () => ({
        userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'), // 当前用户信息
        isLogin: !!localStorage.getItem('userInfo') // 是否已登录
    }),

    actions: {
        /**
         * 用户登录
         * @param {Object} userData 登录数据
         * @returns {Promise<boolean>} 是否成功
         */
        async login(userData) {
            try {
                const res = await login(userData);
                if (res.code === 200) {
                    // 保存用户信息（完全依赖登录返回的数据）
                    this.userInfo = res.data;
                    localStorage.setItem('userInfo', JSON.stringify(res.data));
                    this.isLogin = true;
                    ElMessage.success('登录成功');
                    return true;
                } else {
                    ElMessage.error(res.message || '登录失败');
                    return false;
                }
            } catch (error) {
                console.error('登录失败', error);
                ElMessage.error('登录失败，请稍后重试');
                return false;
            }
        },
        /**
         * 用户注册
         * @param {Object} userData 注册数据
         * @returns {Promise<boolean>} 是否成功
         */
        async register(userData) {
            try {
                const res = await register(userData);
                if (res.code === 200) {
                    ElMessage.success('注册成功，请登录');
                    return true;
                } else {
                    ElMessage.error(res.message || '注册失败');
                    return false;
                }
            } catch (error) {
                console.error('注册失败', error);
                ElMessage.error('注册失败，请稍后重试');
                return false;
            }
        },
        /**
         * 更新用户信息
         * @param {Object} userData 新用户信息
         * @returns {Promise<boolean>} 是否成功
         */
        async updateUser(userData) {
            try {
                const res = await updateUser(userData);
                if (res.code === 200) {
                    // 更新成功后，更新本地存储的用户信息
                    this.userInfo = res.data;
                    localStorage.setItem('userInfo', JSON.stringify(res.data));
                    ElMessage.success('信息更新成功');
                    return true;
                } else {
                    ElMessage.error(res.message || '更新失败');
                    return false;
                }
            } catch (error) {
                console.error('更新用户信息失败', error);
                ElMessage.error('更新失败，请稍后重试');
                return false;
            }
        },
        /**
         * 用户退出登录
         */
        logout() {
            this.userInfo = null;
            this.isLogin = false;
            localStorage.removeItem('userInfo');
            ElMessage.success('已退出登录');
        }
    }
});
