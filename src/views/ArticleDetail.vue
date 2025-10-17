<!--
  ArticleDetail 文章详情页
  展示文章内容、元信息、点赞/收藏操作、评论区，支持编辑跳转。
-->
<template>
  <div class="article-detail-container" v-loading="loading">
    <el-row :gutter="20">
      <el-col :xs="24" :md="16" :offset="md ? 4 : 0">
        <!-- 文章内容 -->
        <div class="article-content">
          <h1 class="article-title">{{ article.title }}</h1>

          <!-- 元信息：包含点赞数、收藏数 -->
          <div class="article-meta">
            <span>发布时间: {{ formatTime(article.createdTime) }}</span>
            <span>作者: {{ article.author?.nickname || '未知' }}</span>
            <span>状态: {{ article.status === 1 ? '已发布' : '草稿' }}</span>
            <span>阅读: {{ formatCount(article.viewCount || 0) }}</span>
            <span>点赞: {{ formatCount(article.likeCount || 0) }}</span>
            <span>收藏: {{ formatCount(article.collectionCount || 0) }}</span>
          </div>

          <!-- 点赞/收藏按钮区域 -->
          <div class="article-actions-top">
            <el-button
                type="text"
                class="like-btn"
                :icon="isLiked ? CircleCheckFilled : CircleCheck"
                :class="{ 'liked': isLiked }"
                @click="handleToggleLike"
                :loading="likeLoading"
            >
              {{ isLiked ? '已点赞' : '点赞' }}
            </el-button>

            <el-button
                type="text"
                class="collection-btn"
                :icon="isCollected ? StarFilled : Star"
                :class="{ 'collected': isCollected }"
                @click="handleToggleCollection"
                :loading="collectionLoading"
            >
              {{ isCollected ? '已收藏' : '收藏' }}
            </el-button>
          </div>

          <div class="article-cover" v-if="article.coverImage">
            <el-image :src="article.coverImage" fit="cover"/>
          </div>

          <div class="article-summary">
            <strong>摘要：</strong>{{ article.summary }}
          </div>

          <div class="article-body">
            <div v-html="article.content"></div>
          </div>

          <!-- 底部操作按钮 -->
          <div class="article-actions">
            <el-button @click="$router.push('/articles')">返回列表</el-button>
            <el-button type="primary" @click="$router.push(`/articles/edit/${article.id}`)">
              编辑文章
            </el-button>
          </div>
        </div>

        <!-- 评论区 -->
        <CommentList :articleId="articleId"/>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted, watch} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
// 注意：不单独导入图标，完全依赖全局注册

// 导入API接口
import {
  getArticleDetail,
  toggleArticleLike,
  getArticleLikeStatus,
  toggleArticleCollection,
  getArticleCollectionStatus
} from '../api/article';
import CommentList from '../components/CommentList.vue';
import {CircleCheck, CircleCheckFilled, Star, StarFilled} from "@element-plus/icons-vue";

// 路由参数
const route = useRoute();
const router = useRouter();
const articleId = route.params.id;

// 文章详情数据
const article = ref({});
// 加载状态
const loading = ref(true);
// 点赞相关状态
const isLiked = ref(false); // 是否已点赞
const likeLoading = ref(false); // 点赞加载中
// 收藏相关状态
const isCollected = ref(false); // 是否已收藏
const collectionLoading = ref(false); // 收藏加载中

/**
 * 获取文章详情
 */
const fetchArticleDetail = async () => {
  try {
    loading.value = true;
    const res = await getArticleDetail(articleId);
    if (res.code !== 200) {
      ElMessage.error(`获取文章详情失败：${res.message || '未知错误'}`);
      setTimeout(() => router.push('/articles'), 1500);
      return;
    }
    article.value = res.data || {};
    // 获取详情后查询点赞/收藏状态
    await Promise.all([
      fetchLikeStatus(),
      fetchCollectionStatus()
    ]);
  } catch (error) {
    console.error('获取文章详情失败', error);
    ElMessage.error('获取文章详情失败，请稍后重试');
    setTimeout(() => router.push('/articles'), 1500);
  } finally {
    loading.value = false;
  }
};

/**
 * 查询点赞状态
 */
const fetchLikeStatus = async () => {
  try {
    const res = await getArticleLikeStatus(articleId);
    if (res.code === 200) {
      isLiked.value = res.data;
    }
  } catch (error) {
    console.error('查询点赞状态失败', error);
  }
};

/**
 * 查询收藏状态
 */
const fetchCollectionStatus = async () => {
  try {
    const res = await getArticleCollectionStatus(articleId);
    if (res.code === 200) {
      isCollected.value = res.data;
    }
  } catch (error) {
    console.error('查询收藏状态失败', error);
  }
};

/**
 * 点赞/取消点赞操作
 */
const handleToggleLike = async () => {
  if (likeLoading.value) return;
  likeLoading.value = true;

  try {
    // 提交点赞消息到后端
    const res = await toggleArticleLike(articleId);
    if (res.code !== 200) {
      ElMessage.error(res.message || '点赞操作失败');
      return;
    }

    // 立即更新本地状态
    const newLikeStatus = !isLiked.value;
    isLiked.value = newLikeStatus;

    // 临时更新点赞数
    article.value.likeCount = newLikeStatus
        ? (article.value.likeCount || 0) + 1
        : Math.max(0, (article.value.likeCount || 0) - 1);

    ElMessage.success(newLikeStatus ? '点赞请求已提交' : '取消点赞请求已提交');

    // 延迟同步后端真实状态
    setTimeout(async () => {
      await fetchLikeStatus();
      // 同步最新的点赞数
      const detailRes = await getArticleDetail(articleId);
      if (detailRes.code === 200) {
        article.value.likeCount = detailRes.data.likeCount;
      }
    }, 1500);
  } catch (error) {
    console.error('点赞操作失败', error);
    ElMessage.error('点赞操作失败，请稍后重试');
    // 失败时回滚状态
    isLiked.value = !isLiked.value;
  } finally {
    likeLoading.value = false;
  }
};

/**
 * 收藏/取消收藏操作
 */
const handleToggleCollection = async () => {
  if (collectionLoading.value) return;
  collectionLoading.value = true;

  try {
    // 提交收藏消息到后端
    const res = await toggleArticleCollection(articleId);
    if (res.code !== 200) {
      ElMessage.error(res.message || '收藏操作失败');
      return;
    }

    // 立即更新本地状态
    const newCollectionStatus = !isCollected.value;
    isCollected.value = newCollectionStatus;

    // 临时更新收藏数
    article.value.collectionCount = newCollectionStatus
        ? (article.value.collectionCount || 0) + 1
        : Math.max(0, (article.value.collectionCount || 0) - 1);

    ElMessage.success(newCollectionStatus ? '收藏请求已提交' : '取消收藏请求已提交');

    // 延迟同步后端真实状态
    setTimeout(async () => {
      await fetchCollectionStatus();
      // 同步最新的收藏数
      const detailRes = await getArticleDetail(articleId);
      if (detailRes.code === 200) {
        article.value.collectionCount = detailRes.data.collectionCount;
      }
    }, 1500);
  } catch (error) {
    console.error('收藏操作失败', error);
    ElMessage.error('收藏操作失败，请稍后重试');
    // 失败时回滚状态
    isCollected.value = !isCollected.value;
  } finally {
    collectionLoading.value = false;
  }
};

/**
 * 格式化时间
 * @param {string} timeStr 时间字符串
 * @returns {string}
 */
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  return new Date(timeStr).toLocaleString();
};

/**
 * 格式化数字
 * @param {number} count 数值
 * @returns {string|number}
 */
const formatCount = (count) => {
  if (count === undefined || count === null) return 0;
  return count >= 1000 ? `${(count / 1000).toFixed(1)}k` : count;
};

// 页面加载时获取详情
onMounted(() => {
  fetchArticleDetail();
});

// 监听路由参数变化，自动刷新详情
watch(() => route.params.id, () => {
  fetchArticleDetail();
});
</script>

<style scoped>
.article-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.article-content {
  background-color: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.article-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 20px 0;
  text-align: center;
  line-height: 1.5;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 15px;
  color: #888;
  font-size: 14px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

/* 点赞/收藏按钮样式 */
.article-actions-top {
  display: flex;
  gap: 20px;
  margin: 0 auto 30px;
  justify-content: center;
  padding: 15px 0;
}

.like-btn {
  color: #666;
  font-size: 16px;
  padding: 6px 16px;
  transition: all 0.3s;
}

.like-btn.liked {
  color: #ff4d4f; /* 已点赞状态颜色 */
}

.collection-btn {
  color: #666;
  font-size: 16px;
  padding: 6px 16px;
  transition: all 0.3s;
}

.collection-btn.collected {
  color: #fa8c16; /* 已收藏状态颜色 */
}

.like-btn:hover, .collection-btn:hover {
  background-color: #f5f7fa;
}

.article-cover {
  width: 100%;
  height: 400px;
  margin-bottom: 30px;
  overflow: hidden;
  border-radius: 4px;
}

.article-summary {
  font-size: 16px;
  color: #666;
  padding: 15px;
  background-color: #f5f7fa;
  border-left: 4px solid #409eff;
  margin-bottom: 30px;
  line-height: 1.8;
}

.article-body {
  font-size: 16px;
  line-height: 2;
  color: #333;
  margin-bottom: 40px;
}

.article-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .article-content {
    padding: 20px 15px;
  }

  .article-title {
    font-size: 22px;
  }

  .article-cover {
    height: 200px;
  }

  .article-actions-top {
    gap: 10px;
  }

  .like-btn, .collection-btn {
    font-size: 14px;
    padding: 4px 12px;
  }
}
</style>
