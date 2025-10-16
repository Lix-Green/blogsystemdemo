<template>
  <div class="message-board-card">
    <div class="section-header">
      <span class="section-title">留言板</span>
      <span class="section-badge">欢迎留言交流</span>
    </div>
    <div class="message-list">
      <div v-if="messages.length === 0" class="no-message">
        暂无留言，快来发表你的看法吧！
      </div>
      <div v-for="(msg, idx) in messages" :key="idx" class="message-item">
        <div class="message-avatar">
          <el-avatar :size="32" :src="msg.avatar"/>
        </div>
        <div class="message-content">
          <div class="message-meta">
            <span class="message-user">{{ msg.nickname }}</span>
            <span class="message-time">{{ formatTime(msg.createTime) }}</span>
          </div>
          <div class="message-text">{{ msg.content }}</div>
        </div>
      </div>
    </div>
    <div class="message-input-area">
      <el-input
          v-model="input"
          type="textarea"
          :rows="2"
          placeholder="说点什么..."
          class="message-input"
          maxlength="200"
          show-word-limit
          :disabled="!userStore.isLogin"
      />
      <el-button type="primary" @click="handleSend" :disabled="!input.trim() || !userStore.isLogin" class="send-btn">
        发表留言
      </el-button>
    </div>
    <div v-if="!userStore.isLogin" class="login-tip">请先登录后再留言</div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {ElMessage} from 'element-plus';
import {getMessages, addMessage} from '../api/message';
import {useUserStore} from '../store/user';

const input = ref('');
const messages = ref([]);
const loading = ref(false);
const userStore = useUserStore();

async function fetchMessages() {
  loading.value = true;
  try {
    const res = await getMessages();
    messages.value = Array.isArray(res.data) ? res.data : [];
  } catch (e) {
    ElMessage.error('获取留言失败');
  } finally {
    loading.value = false;
  }
}

async function handleSend() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录后再留言');
    return;
  }
  if (!input.value.trim()) return;
  try {
    await addMessage({
      content: input.value.trim(),
      userId: userStore.userInfo?.id || 0
    });
    input.value = '';
    ElMessage.success('留言成功！');
    fetchMessages();
  } catch (e) {
    ElMessage.error('留言失败');
  }
}

function formatTime(ts) {
  const d = new Date(ts);
  return d.toLocaleString();
}

onMounted(fetchMessages);
</script>

<style scoped>
.message-board-card {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.06);
  padding: 20px;
  margin-top: 30px;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.section-badge {
  font-size: 12px;
  color: #fff;
  background-color: #409eff;
  padding: 2px 8px;
  border-radius: 12px;
  margin-left: 10px;
  letter-spacing: 0.2px;
}

.message-list {
  margin-bottom: 18px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.message-item:last-child {
  border-bottom: none;
}

.message-avatar {
  margin-right: 12px;
}

.message-content {
  flex: 1;
}

.message-meta {
  font-size: 13px;
  color: #888;
  margin-bottom: 4px;
  display: flex;
  gap: 12px;
}

.message-user {
  font-weight: 600;
  color: #409eff;
}

.message-time {
  font-size: 12px;
  color: #aaa;
}

.message-text {
  font-size: 15px;
  color: #333;
  line-height: 1.7;
}

.no-message {
  text-align: center;
  color: #aaa;
  padding: 30px 0;
}

.message-input-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  margin-top: 10px;
}

.message-input {
  flex: 1;
}

.send-btn {
  min-width: 100px;
}

.login-tip {
  color: #fa8c16;
  font-size: 14px;
  margin-top: 8px;
  text-align: center;
}
</style>
