import {createRouter, createWebHashHistory} from 'vue-router';
import Home from '../views/Home.vue';
import ArticleList from '../views/ArticleList.vue';
import ArticleDetail from '../views/ArticleDetail.vue';
import ArticleEdit from '../views/ArticleEdit.vue';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import Profile from '../views/Profile.vue';
import MyCollections from '../views/MyCollections.vue';
// 路由配置
const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/articles',
        name: 'ArticleList',
        component: ArticleList
    },
    {
        path: '/articles/:id',
        name: 'ArticleDetail',
        component: ArticleDetail
    },
    {
        path: '/articles/edit/:id?',
        name: 'ArticleEdit',
        component: ArticleEdit,
        meta: {requiresAuth: true}
    },
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: {requiresGuest: true}
    },
    {
        path: '/register',
        name: 'Register',
        component: Register,
        meta: {requiresGuest: true}
    },
    {
        path: '/profile',
        name: 'Profile',
        component: Profile,
        meta: {requiresAuth: true}
    }
    ,
    {
        path: '/collections',
        name: 'MyCollections',
        component: MyCollections,
        meta: {requiresAuth: true}
    }
];
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
