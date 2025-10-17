// 路由配置文件，统一管理前端页面路由及权限元信息
import {createRouter, createWebHashHistory} from 'vue-router';
import Home from '../views/Home.vue';
import ArticleList from '../views/ArticleList.vue';
import ArticleDetail from '../views/ArticleDetail.vue';
import ArticleEdit from '../views/ArticleEdit.vue';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import Profile from '../views/Profile.vue';
import MyCollections from '../views/MyCollections.vue';

/**
 * routes 路由数组：每个对象代表一个页面路由
 * path 路径，name 路由名称，component 组件，meta 路由元信息（如登录权限）
 */
const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home // 首页
    },
    {
        path: '/articles',
        name: 'ArticleList',
        component: ArticleList // 文章列表页
    },
    {
        path: '/articles/:id',
        name: 'ArticleDetail',
        component: ArticleDetail // 文章详情页
    },
    {
        path: '/articles/edit/:id?',
        name: 'ArticleEdit',
        component: ArticleEdit, // 文章编辑页
        meta: {requiresAuth: true} // 需要登录
    },
    {
        path: '/login',
        name: 'Login',
        component: Login, // 登录页
        meta: {requiresGuest: true} // 仅未登录可访问
    },
    {
        path: '/register',
        name: 'Register',
        component: Register, // 注册页
        meta: {requiresGuest: true}
    },
    {
        path: '/profile',
        name: 'Profile',
        component: Profile, // 个人中心页
        meta: {requiresAuth: true}
    },
    {
        path: '/collections',
        name: 'MyCollections',
        component: MyCollections, // 我的收藏页
        meta: {requiresAuth: true}
    }
];

// 创建并导出路由实例
const router = createRouter({
    history: createWebHashHistory(),
    routes
});

// 路由守卫 - 延迟导入store，确保Pinia已初始化
router.beforeEach(async (to, from, next) => {
    // 在守卫内部导入useUserStore
    const {useUserStore} = await import('../store/user');
    const userStore = useUserStore();

    // 需要登录的页面
    if (to.meta.requiresAuth && !userStore.isLogin) {
        return next({path: '/login', query: {redirect: to.fullPath}});
    }

    // 未登录才能访问的页面
    if (to.meta.requiresGuest && userStore.isLogin) {
        return next({path: '/'});
    }

    next();
});

export default router;
