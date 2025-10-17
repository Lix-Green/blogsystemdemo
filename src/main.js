/*
 * Vue 应用入口文件，负责初始化应用、挂载根组件、引入全局依赖（如路由、状态管理、UI库等）。
 *
 * 文件结构：
 * - 引入样式文件
 * - 导入并创建Pinia实例
 * - 导入路由
 * - 导入并配置Element Plus组件
 * - 创建并挂载Vue应用
 */

import 'quill/dist/quill.snow.css';
import {createPinia} from 'pinia'; // 导入Pinia创建函数
import router from './router';
import 'element-plus/dist/index.css';
import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import App from './App.vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 1. 先创建Pinia实例
const pinia = createPinia();
// 2. 创建Vue应用
const app = createApp(App);
// 3. 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.use(ElementPlus, {size: 'small', zIndex: 3000})
// 4. 先使用Pinia，再使用router和其他插件
app.use(pinia)
    .use(ElementPlus)
    .use(router)
    .mount('#app');
