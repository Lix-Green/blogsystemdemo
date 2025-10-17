/*
* 应用主入口组件，负责全局布局（导航栏、内容区、页脚）和页面结构。
* 包含登录、注册、用户信息、文章发布等主导航。
*/

<template>
  <div id="app">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="app-header">
        <div class="logo" @click="$router.push('/')">文章管理系统</div>

        <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            class="nav-menu"
            background-color="#fff"
            text-color="#333"
            active-text-color="#409eff"
        >
          <el-menu-item index="/" @click="$router.push('/')">
            <el-icon>
              <House/>
            </el-icon>
            首页
          </el-menu-item>
          <el-menu-item index="/articles" @click="$router.push('/articles')">
            <el-icon>
              <Document/>
            </el-icon>
            文章管理
          </el-menu-item>
          <el-menu-item
              index="/articles/edit"
              @click="$router.push('/articles/edit')"
              v-if="userStore.isLogin"
          >
            <el-icon>
              <Edit/>
            </el-icon>
            发布文章
          </el-menu-item>
        </el-menu>

        <!-- 用户相关菜单 -->
        <div class="user-menu" v-if="userStore.isLogin">
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32">
                <img
                    :src="userStore.userInfo?.avatar || 'https://picsum.photos/200/200'"
                    alt="用户头像"
                />
              </el-avatar>
              <span class="username">
                {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}
              </span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/collections')">
                  <el-icon>
                    <Star/>
                  </el-icon>
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/profile')">
                  <el-icon>
                    <User/>
                  </el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item @click="handleLogout">
                  <el-icon>
                    <Close/>
                  </el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div class="auth-buttons" v-else>
          <el-button type="text" @click="$router.push('/login')">登录</el-button>
          <el-button
              type="primary"
              @click="$router.push('/register')"
              style="margin-left: 10px;"
          >
            注册
          </el-button>
        </div>
      </el-header>

      <el-main class="app-main">
        <router-view/>
      </el-main>

      <el-footer class="app-footer">
        文章管理系统 ©{{ new Date().getFullYear() }} 版权所有
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import {computed, onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {useUserStore} from './store/user';
import {Close} from "@element-plus/icons-vue";
// 注意：不需要导入任何图标，因为已经在main.js中全局注册

// 用户状态
const userStore = useUserStore();
const router = useRouter();

// 计算当前激活的菜单
const route = useRoute();
const activeMenu = computed(() => {
  return route.path || '/';
});

// 退出登录
const handleLogout = () => {
  userStore.logout();
  router.push('/');
};

// 页面加载时检查登录状态
onMounted(() => {
  if (userStore.isLogin) {
    // 不需要调用fetchCurrentUser
  }
});
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.app-header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 0 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
  cursor: pointer;
  letter-spacing: 0.5px;
}

.nav-menu {
  flex: 0 0 auto;
  height: 100%;
  margin: 0 20px;
}

/* 用户菜单样式 */
.user-menu {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
}

/* 未登录时的按钮样式 */
.auth-buttons {
  display: flex;
  align-items: center;
}

.app-main {
  flex: 1;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.app-footer {
  text-align: center;
  padding: 16px 0;
  color: #888;
  font-size: 14px;
  border-top: 1px solid #eee;
  background-color: #fff;
}
</style>
