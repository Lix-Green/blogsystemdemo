<!--
  MessageBoard 留言板组件
  用于展示留言列表、发送新留言，支持登录判断和时间格式化。
-->
<template>
  <div class="message-board-card">
    <div class="section-header">
      <span class="section-title">留言板</span>
      <span class="info-tip">只展示最近的20条</span>
    </div>
    <div class="message-list">
      <div v-if="messages.length === 0" class="no-message">
        暂无留言，快来发表你的看法吧！
      </div>
      <div v-for="(msg, idx) in messages" :key="idx" class="message-item">
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

// 留言输入框内容
const input = ref('');
// 留言列表数据
const messages = ref([]);
// 加载状态
const loading = ref(false);

// 用户信息与登录状态
const userStore = useUserStore();

/**
 * 获取留言列表
 */
async function fetchMessages() {
  loading.value = true;
  try {
    const res = await getMessages();
    let arr = Array.isArray(res.data) ? res.data : [];
    // 按时间降序排序并只取最新20条
    arr = arr.sort((a, b) => new Date(b.createTime) - new Date(a.createTime)).slice(0, 20);
    messages.value = arr;
  } catch (e) {
    ElMessage.error('获取留言失败');
  } finally {
    loading.value = false;
  }
}

/**
 * 发送留言
 */
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

/**
 * 时间格式化工具
 * @param {string|number} ts 时间戳
 * @returns {string}
 */
function formatTime(ts) {
  const d = new Date(ts);
  return d.toLocaleString();
}

// 组件挂载时加载留言列表
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
  padding: ;
}

.info-tip {
  font-size: 13px;
  color: #888;
  margin-left: auto;
  padding-left: 12px;
  font-style: italic;
  letter-spacing: 1px;
  align-self: flex-end;
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
