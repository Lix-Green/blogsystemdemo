<!--
  CommentList 评论列表组件
  用于展示指定文章的评论列表，支持发表评论、删除评论、表单校验和登录判断。
-->
<template>
  <div class="comment-container">
    <h3>评论区</h3>

    <!-- 评论输入框 - 未登录时禁用 -->
    <el-form
        ref="commentFormRef"
        :model="commentForm"
        :rules="commentRules"
        class="comment-form"
        v-loading="submitting"
        :disabled="!userStore.isLogin"
    >
      <el-form-item prop="content">
        <el-input
            v-model="commentForm.content"
            placeholder="请先登录再发表评论"
            type="textarea"
            rows="3"
        />
      </el-form-item>
      <el-form-item>
        <el-button
            type="primary"
            @click="handleCommentSubmit"
            :disabled="!userStore.isLogin"
        >
          {{ userStore.isLogin ? '提交评论' : '请登录' }}
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 评论列表 -->
    <div class="comment-list" v-if="comments.length > 0">
      <div class="comment-item" v-for="comment in comments" :key="comment.id">
        <div class="comment-header">
          <!-- 从user.nickname获取用户名 -->
          <span class="comment-author">{{ comment.user?.nickname || '用户' }}</span>
          <span class="comment-time">{{ formatTime(comment.createdTime) }}</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-actions">
          <!-- 只有当前用户的评论才显示删除按钮 -->
          <el-button
              type="text"
              size="small"
              @click="handleDeleteComment(comment.id)"
              class="delete-btn"
              v-if="userStore.isLogin && comment.user?.id === userStore.userInfo?.id"
          >
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 无评论提示 -->
    <div class="no-comments" v-else>
      暂无评论，快来发表第一条评论吧~
    </div>
  </div>
</template>

<script setup>
// 引入 Vue 相关 API 和 Element Plus 组件
import {ref, reactive, watch, onMounted} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {createComment, getArticleComments, deleteComment} from '../api/comment';
import {useUserStore} from '../store/user';

/**
 * Props：接收父组件传递的文章ID
 * @property {Number} articleId 文章ID，必填
 */
const props = defineProps({
  articleId: {
    type: Number,
    required: true
  }
});

// 获取用户store
const userStore = useUserStore();

/**
 * 评论表单数据
 */
const commentForm = reactive({
  content: '',
  articleId: props.articleId,
  parentId: 0
});

/**
 * 评论表单提交
 */
const handleCommentSubmit = async () => {
  if (!props.articleId) {
    ElMessage.error('文章ID不存在，无法评论');
    return;
  }

  // 检查用户是否登录
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录再评论');
    return;
  }

  try {
    await commentFormRef.value.validate();
    submitting.value = true;

    commentForm.articleId = props.articleId;
    const res = await createComment(commentForm);

    if (res.code === 200) {
      ElMessage.success('评论成功');
      commentForm.content = ''; // 清空评论内容
      fetchComments(); // 刷新评论列表
    } else {
      ElMessage.error(`评论失败：${res.message || '未知错误'}`);
    }
  } catch (error) {
    console.error('评论失败', error);
    if (!error.message.includes('取消')) {
      ElMessage.error('评论失败，请稍后重试');
    }
  } finally {
    submitting.value = false;
  }
};

/**
 * 评论表单校验规则
 */
const commentRules = {
  content: [
    {required: true, message: '请输入评论内容', trigger: 'blur'},
    {max: 500, message: '评论内容不能超过500字', trigger: 'blur'}
  ]
};
const commentFormRef = ref(null);

/**
 * 评论列表数据
 */
const comments = ref([]);
// 加载状态
const loading = ref(false);
// 提交状态
const submitting = ref(false);

/**
 * 获取评论列表
 */
const fetchComments = async () => {
  if (!props.articleId) return;

  try {
    loading.value = true;
    const res = await getArticleComments(props.articleId);

    // 正确解析后端返回格式：{code:200, message:"success", data:[评论数组]}
    if (res.code === 200 && Array.isArray(res.data)) {
      comments.value = res.data;
    } else {
      comments.value = [];
      ElMessage.warning('未获取到评论数据');
    }
  } catch (error) {
    console.error('获取评论失败', error);
    ElMessage.error('获取评论失败');
  } finally {
    loading.value = false;
  }
};

/**
 * 删除评论
 */
const handleDeleteComment = async (id) => {
  // 检查登录状态
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录');
    return;
  }

  try {
    await ElMessageBox.confirm(
        '确定要删除这条评论吗？',
        '确认删除',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        }
    );

    // 从userStore获取用户ID并通过请求头传递
    const res = await deleteComment(id, userStore.userInfo.id);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      fetchComments(); // 刷新评论列表
    } else {
      ElMessage.error(`删除失败：${res.message || '未知错误'}`);
    }
  } catch (error) {
    console.error('删除评论失败', error);
    if (!error.message.includes('cancel')) {
      ElMessage.error('删除失败，请稍后重试');
    }
  }
};

/**
 * 格式化时间
 * @param {string} timeStr 时间字符串
 * @returns {string}
 */
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  return date.toLocaleString();
};

// 组件挂载时加载评论列表
onMounted(() => {
  fetchComments();
});

// 监听文章ID变化，自动刷新评论列表
watch(() => props.articleId, () => {
  fetchComments();
});
</script>

<style scoped>
.comment-container {
  margin-top: 30px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.comment-container h3 {
  margin-top: 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.comment-form {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 500;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  margin-bottom: 10px;
  line-height: 1.6;
  color: #666;
}

.comment-actions {
  text-align: right;
}

.delete-btn {
  color: #ff4d4f;
  font-size: 12px;
}

.no-comments {
  text-align: center;
  padding: 30px;
  color: #999;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin-top: 20px;
}
</style>
