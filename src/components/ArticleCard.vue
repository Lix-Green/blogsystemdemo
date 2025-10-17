<!--
  ArticleCard 文章卡片组件
  用于展示单篇文章的封面、标题、摘要、作者、时间、阅读量、点赞数、收藏数等信息。
  支持点击跳转详情、点赞、收藏等交互。
-->
<template>
  <div class="article-card" @click="handleCardClick">
    <!-- 文章封面 -->
    <div class="card-cover" v-if="article.coverImage">
      <el-image :src="article.coverImage" fit="cover"/>
    </div>

    <!-- 文章内容 -->
    <div class="card-content">
      <!-- 标题 -->
      <h3 class="card-title">{{ article.title }}</h3>

      <!-- 摘要 -->
      <p class="card-summary" v-if="article.summary">
        {{ article.summary }}
      </p>

      <!-- 元信息：作者、时间、阅读量、点赞数、收藏数 -->
      <div class="card-meta">
        <span class="meta-item">
          <el-icon size="14"><User/></el-icon>
          {{ article.author?.nickname || '未知作者' }}
        </span>
        <span class="meta-item">
          <el-icon size="14"><Calendar/></el-icon>
          {{ formatTime(article.createdTime) }}
        </span>
        <span class="meta-item">
          <el-icon size="14"><View/></el-icon>
          {{ formatCount(article.viewCount) }}
        </span>
        <!-- 点赞数 -->
        <span class="meta-item" @click.stop="handleCardLike">
          <el-icon size="14" class="like-icon">
            <CircleCheck :color="isCardLiked ? '#ff4d4f' : '#666'"/>
          </el-icon>
          {{ formatCount(article.likeCount || 0) }}
        </span>
        <!-- 收藏数 -->
        <span class="meta-item" @click.stop="handleCardCollection">
          <el-icon size="14" class="collection-icon">
            <Star :color="isCardCollected ? '#fa8c16' : '#666'"/>
          </el-icon>
          {{ formatCount(article.collectionCount || 0) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
// 引入 Vue 相关 API
import {ref, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
// 注意：这里不再单独导入任何图标，完全依赖全局注册

// 引入文章相关 API
import {
  getArticleDetail,
  getArticleLikeStatus,
  toggleArticleLike,
  getArticleCollectionStatus,
  toggleArticleCollection
} from '../api/article';

/**
 * 接收父组件传递的 article 数据
 * @property {Object} article 文章对象，包含所有展示信息
 */
const props = defineProps({
  article: {
    type: Object,
    required: true,
    default: () => ({})
  }
});

// 初始化路由实例
const router = useRouter();

// 卡片点赞/收藏状态
const isCardLiked = ref(false); // 是否已点赞
const isCardCollected = ref(false); // 是否已收藏
const likeLoading = ref(false); // 点赞操作加载状态
const collectionLoading = ref(false); // 收藏操作加载状态

/**
 * 初始化卡片点赞/收藏状态
 */
const initCardStatus = async () => {
  try {
    // 并行获取点赞和收藏状态
    const [likeRes, collectionRes] = await Promise.all([
      getArticleLikeStatus(props.article.id),
      getArticleCollectionStatus(props.article.id)
    ]);

    if (likeRes.code === 200) {
      isCardLiked.value = likeRes.data;
    }

    if (collectionRes.code === 200) {
      isCardCollected.value = collectionRes.data;
    }
  } catch (error) {
    console.error('初始化卡片状态失败', error);
  }
};

/**
 * 格式化时间：将时间字符串转为本地日期格式
 * @param {string} timeStr 时间字符串
 * @returns {string}
 */
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  return new Date(timeStr).toLocaleDateString();
};

/**
 * 格式化数字：超过 1000 显示为 k 单位（如 1200 → 1.2k）
 * @param {number} count 数值
 * @returns {string|number}
 */
const formatCount = (count) => {
  if (count === undefined || count === null) return 0;
  return count >= 1000 ? `${(count / 1000).toFixed(1)}k` : count;
};

/**
 * 点击卡片跳转至文章详情页
 */
const handleCardClick = () => {
  if (props.article.id) {
    router.push(`/articles/${props.article.id}`);
  }
};

/**
 * 卡片点赞逻辑
 */
const handleCardLike = async () => {
  if (likeLoading.value) return;
  likeLoading.value = true;

  try {
    // 提交点赞消息到后端
    const res = await toggleArticleLike(props.article.id);
    if (res.code !== 200) {
      ElMessage.error(res.message || '点赞操作失败');
      return;
    }

    // 立即更新本地状态
    const newLikeStatus = !isCardLiked.value;
    isCardLiked.value = newLikeStatus;

    // 临时更新点赞数
    props.article.likeCount = newLikeStatus
        ? (props.article.likeCount || 0) + 1
        : Math.max(0, (props.article.likeCount || 0) - 1);

    ElMessage.success(newLikeStatus ? '点赞请求已提交' : '取消点赞请求已提交');

    // 延迟同步后端真实状态
    setTimeout(async () => {
      const statusRes = await getArticleLikeStatus(props.article.id);
      if (statusRes.code === 200) {
        isCardLiked.value = statusRes.data;
        // 同步最新点赞数
        const detailRes = await getArticleDetail(props.article.id);
        if (detailRes.code === 200) {
          props.article.likeCount = detailRes.data.likeCount;
        }
      }
    }, 1500);
  } catch (error) {
    console.error('卡片点赞失败', error);
    ElMessage.error('点赞失败，请稍后重试');
    // 失败时回滚状态
    isCardLiked.value = !isCardLiked.value;
  } finally {
    likeLoading.value = false;
  }
};

/**
 * 卡片收藏逻辑
 */
const handleCardCollection = async () => {
  if (collectionLoading.value) return;
  collectionLoading.value = true;

  try {
    // 提交收藏消息到后端
    const res = await toggleArticleCollection(props.article.id);
    if (res.code !== 200) {
      ElMessage.error(res.message || '收藏操作失败');
      return;
    }

    // 立即更新本地状态
    const newCollectionStatus = !isCardCollected.value;
    isCardCollected.value = newCollectionStatus;

    // 临时更新收藏数
    props.article.collectionCount = newCollectionStatus
        ? (props.article.collectionCount || 0) + 1
        : Math.max(0, (props.article.collectionCount || 0) - 1);

    ElMessage.success(newCollectionStatus ? '收藏请求已提交' : '取消收藏请求已提交');

    // 延迟同步后端真实状态
    setTimeout(async () => {
      const statusRes = await getArticleCollectionStatus(props.article.id);
      if (statusRes.code === 200) {
        isCardCollected.value = statusRes.data;
        // 同步最新收藏数
        const detailRes = await getArticleDetail(props.article.id);
        if (detailRes.code === 200) {
          props.article.collectionCount = detailRes.data.collectionCount;
        }
      }
    }, 1500);
  } catch (error) {
    console.error('卡片收藏失败', error);
    ElMessage.error('收藏失败，请稍后重试');
    // 失败时回滚状态
    isCardCollected.value = !isCardCollected.value;
  } finally {
    collectionLoading.value = false;
  }
};

// 页面加载时初始化状态
onMounted(() => {
  initCardStatus();
});
</script>

<style scoped>
.article-card {
  margin-bottom: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  width: 100%;
  height: 340px; /* 固定卡片高度，可根据实际页面调整 */
  display: flex;
  flex-direction: column;
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.card-cover {
  width: 100%;
  height: 200px; /* 固定封面高度 */
  overflow: hidden;
  flex-shrink: 0;
}

.card-cover el-image,
.card-cover .el-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
  min-height: 0;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #888;
  margin-top: auto; /* 保证元信息在底部 */
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: color 0.2s;
}

.meta-item:hover {
  color: #409eff;
}

.like-icon:hover {
  color: #ff4d4f !important;
}

.collection-icon:hover {
  color: #fa8c16 !important;
}
</style>
