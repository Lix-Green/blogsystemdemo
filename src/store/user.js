import {defineStore} from 'pinia';
import {login, register, updateUser} from '../api/user';
import {ElMessage} from 'element-plus';

export const useUserStore = defineStore('user', {
    state: () => ({
        userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
        isLogin: !!localStorage.getItem('userInfo') // 基于用户信息判断登录状态
    }),

    actions: {
        // 用户登录
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
        // 用户注册
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

        // 退出登录
        logout() {
            this.userInfo = null;
            this.isLogin = false;
            localStorage.removeItem('userInfo');
            ElMessage.success('已退出登录');
        },

        // 更新用户信息
        async updateUserInfo(id, userData) {
            try {
                const res = await updateUser(id, userData);
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
        }
    }
});
