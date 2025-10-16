<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-title">登录</div>

      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
      >
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
          >
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
          >
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              class="login-btn"
              @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="register-link">
          还没有账号？<a @click="$router.push('/register')">立即注册</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue';
import {useRouter} from 'vue-router';
import {useUserStore} from '../store/user';

// 表单数据
const loginForm = reactive({
  username: '',
  password: ''
});

// 表单验证规则
const loginRules = {
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'}
  ]
};

const loginFormRef = ref(null);
const router = useRouter();
const userStore = useUserStore();
// 处理登录
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate();
    const success = await userStore.login(loginForm);
    if (success) {
      // 登录成功，跳转到首页或之前的页面
      const redirect = router.currentRoute.value.query.redirect || '/';
      router.push(redirect);
    }
  } catch (error) {
    console.error('登录表单验证失败', error);
  }
};
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.auth-card {
  width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.auth-title {
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #333;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
}

.register-link {
  text-align: center;
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
