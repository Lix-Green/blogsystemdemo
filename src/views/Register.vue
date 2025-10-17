<!--
  Register 用户注册页面
  提供注册表单、校验、跳转登录等功能。
-->
<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-title">注册</div>

      <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          label-width="120px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
          >
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>


        <el-form-item label="密码" prop="password">
          <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码（至少6位）"
          >
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input
              v-model="registerForm.nickname"
              placeholder="请输入昵称"
          >
            <template #prefix>
              <el-icon>
                <UserFilled/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱"
          >
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              class="register-btn"
              @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="login-link">
          已有账号？<a @click="$router.push('/login')">立即登录</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue';
import {useRouter} from 'vue-router';
import {useUserStore} from '../store/user';
import {ElMessage} from 'element-plus';


const registerForm = reactive({
  username: '',
  password: '',
  nickname: '',
  email: ''
});

// 表单引用
const registerFormRef = ref(null);

/**
 * registerForm 注册表单数据
 * registerRules 表单校验规则
 * registerFormRef 表单引用
 */

// 验证规则 - 移除确认密码相关规则
const registerRules = {
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
    {min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, message: '密码长度不能少于6位', trigger: 'blur'}
  ],
  nickname: [
    {required: true, message: '请输入昵称', trigger: 'blur'},
    {max: 20, message: '昵称不能超过20个字符', trigger: 'blur'}
  ],
  email: [
    {required: true, message: '请输入邮箱', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change']}
  ]
};

// 路由和状态管理
const router = useRouter();
const userStore = useUserStore();

/**
 * 注册处理方法
 */
// 处理注册
const handleRegister = async () => {
  try {
    // 验证表单
    await registerFormRef.value.validate();

    // 准备提交数据（无需再删除confirmPassword）
    const registerData = {...registerForm};

    // 调用注册接口
    const success = await userStore.register(registerData);
    if (success) {
      ElMessage.success('注册成功，请登录');
      router.push('/login');
    }
  } catch (error) {
    console.error('注册失败:', error);
    if (error.name !== 'ValidationError') {
      ElMessage.error('注册失败，请稍后重试');
    }
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
  width: 450px;
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

.register-form {
  margin-top: 20px;
}

.register-btn {
  width: 100%;
}

.login-link {
  text-align: center;
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
