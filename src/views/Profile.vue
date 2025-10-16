<template>
  <div class="profile-container" v-loading="loading">
    <div class="profile-header">
      <h2>个人信息</h2>
    </div>

    <div class="profile-card">
      <div class="avatar-section">
        <el-avatar :size="100" class="avatar">
          <img
              :src="userForm.avatar || 'https://picsum.photos/200/200'"
              alt="用户头像"
          />
        </el-avatar>
        <div class="avatar-upload">
          <el-button type="text" @click="handleAvatarChange">更换头像</el-button>
        </div>
      </div>

      <el-form
          ref="userFormRef"
          :model="userForm"
          :rules="userRules"
          class="user-form"
          label-width="120px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" disabled/>
          <div class="form-hint">用户名不可修改</div>
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname"/>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email"/>
        </el-form-item>

        <el-form-item label="注册时间">
          <el-input
              :value="formatTime(userForm.createdTime)"
              disabled
          />
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="userForm.status" disabled>
            <el-option label="正常" value="1"/>
            <el-option label="禁用" value="0"/>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleUpdate">保存修改</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button
              type="danger"
              @click="handleDeleteAccount"
              style="margin-left: 10px;"
          >
            删除账号
          </el-button>
          <el-button
              type="warning"
              @click="handleLogout"
              style="margin-left: 10px;"
          >
            退出登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, toRaw} from 'vue';
import {useRouter} from 'vue-router';
import {useUserStore} from '../store/user';
import {deleteUser} from '../api/user';
import {ElMessage, ElMessageBox} from 'element-plus';

// 状态管理
const userStore = useUserStore();
const router = useRouter();
const loading = ref(true);
const userFormRef = ref(null);

// 用户表单数据
const userForm = reactive({
  id: '',
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  status: 1,
  createdTime: null,
  updatedTime: null
});

// 表单验证规则
const userRules = {
  nickname: [
    {required: true, message: '请输入昵称', trigger: 'blur'},
    {max: 20, message: '昵称不能超过20个字符', trigger: 'blur'}
  ],
  email: [
    {required: true, message: '请输入邮箱', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change']}
  ]
};

// 初始化用户信息（从登录存储中获取）
const initUserInfo = () => {
  try {
    loading.value = true;
    // 检查是否已登录
    if (!userStore.isLogin || !userStore.userInfo) {
      router.push('/login?redirect=/profile');
      return;
    }

    // 从存储中获取用户信息
    const userInfo = toRaw(userStore.userInfo);
    Object.assign(userForm, userInfo);

  } catch (error) {
    console.error('初始化用户信息失败', error);
    ElMessage.error('获取用户信息失败');
    router.push('/login?redirect=/profile');
  } finally {
    loading.value = false;
  }
};

// 处理更新用户信息
const handleUpdate = async () => {
  try {
    await userFormRef.value.validate();
    // 准备要提交的数据
    const updateData = {
      nickname: userForm.nickname,
      email: userForm.email,
      avatar: userForm.avatar,
      status: userForm.status
    };

    const success = await userStore.updateUserInfo(userForm.id, updateData);
    if (success) {
      // 更新成功后重新初始化表单
      initUserInfo();
    }
  } catch (error) {
    console.error('更新用户信息失败', error);
  }
};

// 重置表单
const handleReset = () => {
  userFormRef.value.resetFields();
};

// 退出登录
const handleLogout = () => {
  userStore.logout();
  router.push('/login');
};

// 删除账号
const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
        '确定要删除您的账号吗？此操作不可撤销！',
        '警告',
        {
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          type: 'error'
        }
    );

    const res = await deleteUser(userForm.id);
    if (res.code === 200) {
      ElMessage.success('账号已删除');
      userStore.logout();
      router.push('/login');
    } else {
      ElMessage.error(`删除失败：${res.message || '未知错误'}`);
    }
  } catch (error) {
    console.error('删除账号失败', error);
    if (!error.message.includes('cancel')) {
      ElMessage.error('删除失败，请稍后重试');
    }
  }
};

// 更换头像
const handleAvatarChange = () => {
  const randomId = Math.floor(Math.random() * 100);
  userForm.avatar = `https://picsum.photos/id/${randomId}/200/200`;
  ElMessage.success('头像已更换，点击保存修改生效');
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '未知';
  return new Date(timeStr).toLocaleString();
};

// 页面加载时初始化用户信息
onMounted(() => {
  initUserInfo();
});
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.profile-card {
  background-color: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.avatar {
  margin-right: 20px;
}

.avatar-upload {
  margin-top: 10px;
}

.user-form {
  margin-top: 20px;
}

.form-hint {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}
</style>
