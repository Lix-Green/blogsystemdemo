<template>
  <div class="home-container">
    <!-- 页面标题 -->
    <el-row :gutter="20" class="page-header">
      <el-col :span="24">
        <div class="main-title">文章广场</div>
        <div class="subtitle">发现最新内容，探索热门话题</div>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <el-row :gutter="20" class="search-section">
      <el-col :span="24">
        <div class="search-card">
          <el-form :model="searchForm" @submit.prevent="handleSearch">
            <el-input
                v-model="searchForm.keyword"
                placeholder="搜索文章标题、内容或关键词..."
                size="large"
                class="search-input"
                clearable
                @clear="handleClearSearch"
                @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon>
                  <Search/>
                </el-icon>
              </template>
              <template #append>
                <el-button
                    type="primary"
                    :icon="Search"
                    @click="handleSearch"
                    :loading="searchLoading"
                >
                  搜索
                </el-button>
              </template>
            </el-input>

            <!-- 搜索统计和筛选 -->
            <div class="search-meta" v-if="showSearchResults">
              <span class="search-count">
                找到 {{ filteredArticles.length }} 条结果
                <span v-if="searchForm.keyword">关键词 "{{ searchForm.keyword }}"</span>
              </span>
              <el-button
                  type="text"
                  @click="handleClearSearch"
                  class="clear-search"
              >
                清除搜索
              </el-button>
            </div>
          </el-form>
        </div>
      </el-col>
    </el-row>

    <!-- 核心内容区：最新文章 + 热门文章 分栏布局 -->
    <el-row :gutter="30" class="content-wrapper">
      <!-- 左侧：最新文章/搜索结果 -->
      <el-col :xs="24" :md="showSearchResults ? 24 : 16">
        <div class="section-header">
          <span class="section-title">
            {{ showSearchResults ? '搜索结果' : '最新文章' }}
          </span>
          <span class="section-badge">
            {{ showSearchResults ? '按相关度排序' : '按发布时间排序' }}
          </span>
        </div>

        <!-- 文章列表 -->
        <el-row :gutter="20" class="latest-article-list">
          <!-- 搜索结果或最新文章 -->
          <el-col
              :xs="24"
              :sm="showSearchResults ? 24 : 12"
              :md="showSearchResults ? 12 : 12"
              v-for="article in displayArticles"
              :key="article.id"
          >
            <ArticleCard :article="article"/>
          </el-col>

          <!-- 搜索无结果提示 -->
          <el-col :span="24" v-if="showSearchResults && filteredArticles.length === 0" class="empty-tip">
            <el-empty description="没有找到相关文章，换个关键词试试吧~">
              <template #bottom>
                <el-button type="primary" @click="handleClearSearch">
                  查看全部文章
                </el-button>
              </template>
            </el-empty>
          </el-col>

          <!-- 无最新文章提示 -->
          <el-col :span="24" v-if="!showSearchResults && articles.length === 0 && !loading" class="empty-tip">
            <el-empty description="暂无最新文章，快去发布你的第一篇吧~">
              <template #bottom>
                <el-button type="primary" @click="$router.push('/articles/edit')">
                  发布文章
                </el-button>
              </template>
            </el-empty>
          </el-col>
        </el-row>

        <!-- 加载更多（搜索结果） -->
        <div class="load-more" v-if="showSearchResults && filteredArticles.length > 0">
          <el-button
              type="text"
              @click="handleClearSearch"
              class="load-more-btn"
          >
            <el-icon>
              <Refresh/>
            </el-icon>
            返回全部文章
          </el-button>
        </div>
      </el-col>

      <!-- 右侧：热门文章（搜索时隐藏） -->
      <el-col :xs="24" :md="8" v-if="!showSearchResults">
        <div class="section-header">
          <span class="section-title">热门文章</span>
          <span class="section-badge">按阅读量排序</span>
        </div>

        <!-- 热门文章卡片容器 -->
        <div class="hot-article-card">
          <el-skeleton
              :loading="hotLoading"
              row-count="5"
              :title="false"
              class="hot-skeleton"
              v-if="hotLoading"
          ></el-skeleton>

          <div v-else>
            <div class="hot-article-item" v-for="(item, index) in hotArticles" :key="item.id">
              <div class="rank-icon" :class="getRankClass(index)">
                {{ index + 1 }}
              </div>

              <div class="article-info">
                <div class="article-title" @click="handleArticleClick(item.id)">
                  {{ item.title }}
                </div>

                <div class="article-meta">
                  <span class="meta-item">
                    <el-icon size="14" class="meta-icon"><View/></el-icon>
                    {{ formatCount(item.viewCount) }} 阅读
                  </span>
                  <span class="meta-item">
                    <el-icon size="14" class="meta-icon"><Message/></el-icon>
                    {{ formatCount(item.commentCount) }} 评论
                  </span>
                </div>
              </div>
            </div>

            <div class="no-hot-articles" v-if="hotArticles.length === 0">
              <el-empty description="暂无热门文章，互动越多越容易上榜哦~"/>
            </div>
          </div>
        </div>

        <!-- 留言板组件放在热门文章区下方 -->
        <MessageBoard/>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue';
import {useRouter} from 'vue-router';
import {getLatestArticles, getHotArticles} from '../api/article';
import ArticleCard from '../components/ArticleCard.vue';
import MessageBoard from '../components/MessageBoard.vue';
import {ElMessage, ElEmpty, ElIcon, ElSkeleton} from 'element-plus';
import {Search, Refresh, View} from '@element-plus/icons-vue';

const router = useRouter();

// 搜索相关状态
const searchForm = ref({
  keyword: ''
});
const searchLoading = ref(false);
const showSearchResults = ref(false);

// 最新文章状态
const articles = ref([]);
const loading = ref(true);

// 热门文章状态
const hotArticles = ref([]);
const hotLoading = ref(true);

// 计算属性：显示的文章列表
const displayArticles = computed(() => {
  return showSearchResults.value ? filteredArticles.value : articles.value;
});

// 计算属性：过滤后的文章（前端搜索逻辑）
const filteredArticles = computed(() => {
  if (!searchForm.value.keyword.trim()) {
    return articles.value;
  }

  const keyword = searchForm.value.keyword.toLowerCase();
  return articles.value.filter(article => {
    // 搜索标题、内容、摘要、标签等
    return (
        (article.title && article.title.toLowerCase().includes(keyword)) ||
        (article.content && article.content.toLowerCase().includes(keyword)) ||
        (article.summary && article.summary.toLowerCase().includes(keyword)) ||
        (article.tags && article.tags.some(tag =>
            tag.toLowerCase().includes(keyword)
        ))
    );
  });
});

// 搜索处理函数
const handleSearch = async () => {
  const keyword = searchForm.value.keyword.trim();

  if (!keyword) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }

  searchLoading.value = true;
  showSearchResults.value = true;

  try {
    // 模拟搜索延迟
    await new Promise(resolve => setTimeout(resolve, 300));

    if (filteredArticles.value.length === 0) {
      ElMessage.info(`没有找到包含"${keyword}"的文章`);
    } else {
      ElMessage.success(`找到 ${filteredArticles.value.length} 条相关文章`);
    }
  } catch (error) {
    console.error('搜索失败', error);
    ElMessage.error('搜索失败，请稍后重试');
  } finally {
    searchLoading.value = false;
  }
};

// 清除搜索
const handleClearSearch = () => {
  searchForm.value.keyword = '';
  showSearchResults.value = false;
  ElMessage.info('已显示全部文章');
};

// 获取最新文章
const fetchLatestArticles = async () => {
  try {
    loading.value = true;
    const res = await getLatestArticles();
    if (res.code !== 200) {
      ElMessage.error(`获取最新文章失败：${res.message || '未知错误'}`);
      articles.value = [];
      return;
    }
    const articleData = res.data || {};
    articles.value = Array.isArray(articleData.articles) ? articleData.articles : [];
  } catch (error) {
    console.error('获取最新文章失败', error);
    articles.value = [];
    ElMessage.error('获取最新文章失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 获取热门文章
const fetchHotArticles = async () => {
  try {
    hotLoading.value = true;
    const res = await getHotArticles(1, 5);
    if (res.code === 200) {
      hotArticles.value = res.data.articles || [];
    } else {
      hotArticles.value = [];
      ElMessage.error('获取热门文章失败');
    }
  } catch (error) {
    console.error('获取热门文章失败', error);
    hotArticles.value = [];
    ElMessage.error('获取热门文章失败，请稍后重试');
  } finally {
    hotLoading.value = false;
  }
};

// 排名样式
const getRankClass = (index) => {
  const rankStyles = {
    0: 'rank-first',
    1: 'rank-second',
    2: 'rank-third'
  };
  return rankStyles[index] || 'rank-other';
};

// 格式化数字
const formatCount = (count) => {
  if (count === undefined || count === null) return 0;
  if (count >= 1000) {
    return `${(count / 1000).toFixed(1)}k`;
  }
  return count;
};

// 文章点击跳转
const handleArticleClick = (articleId) => {
  if (articleId) {
    router.push(`/articles/${articleId}`);
  } else {
    ElMessage.warning('文章ID无效');
  }
};

// 页面加载初始化
onMounted(() => {
  fetchLatestArticles();
  fetchHotArticles();
});
</script>

<style scoped>
/* 首页容器基础样式 */
.home-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  background-color: #fafafa;
  min-height: calc(100vh - 120px);
}

/* 页面标题样式 */
.page-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.main-title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
  letter-spacing: 0.5px;
}

.subtitle {
  font-size: 16px;
  color: #7f8c8d;
  letter-spacing: 0.3px;
}

/* 搜索区域样式 */
.search-section {
  margin-bottom: 30px;
}

.search-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-input {
  margin-bottom: 12px;
}

.search-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.search-count {
  font-size: 14px;
  color: #666;
}

.clear-search {
  color: #409eff;
  font-size: 13px;
}

/* 分栏模块标题 */
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

/* 文章列表样式 */
.latest-article-list {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
}

.empty-tip {
  margin-top: 60px;
  padding: 40px 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 加载更多按钮 */
.load-more {
  text-align: center;
  margin-top: 30px;
  padding: 20px;
}

.load-more-btn {
  color: #409eff;
  font-size: 14px;
}

/* 热门文章卡片 */
.hot-article-card {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.06);
  padding: 20px;
  margin-top: 10px;
}

/* 热门文章骨架屏 */
.hot-skeleton {
  padding: 12px 0;
  gap: 12px;
}

/* 单条热门文章 */
.hot-article-item {
  display: flex;
  align-items: flex-start;
  padding: 14px 0;
  border-bottom: 1px dashed #f0f0f0;
  cursor: pointer;
  transition: all 0.25s ease;
}

.hot-article-item:last-child {
  border-bottom: none;
}

.hot-article-item:hover {
  background-color: #f8fafc;
  transform: translateX(3px);
}

/* 排名标识 */
.rank-icon {
  width: 26px;
  height: 26px;
  line-height: 26px;
  text-align: center;
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  border-radius: 50%;
  margin-right: 14px;
  flex-shrink: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.rank-first {
  background-color: #ff4d4f;
}

.rank-second {
  background-color: #fa8c16;
}

.rank-third {
  background-color: #1890ff;
}

.rank-other {
  background-color: #94a3b8;
  font-weight: 500;
}

/* 文章信息区 */
.article-info {
  flex: 1;
  padding: 2px 0;
}

/* 文章标题 */
.article-title {
  font-size: 15.5px;
  color: #2d3748;
  margin-bottom: 7px;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.25s ease;
  letter-spacing: 0.2px;
}

.article-title:hover {
  color: #1890ff;
  text-decoration: underline;
  text-underline-offset: 3px;
}

/* 文章统计 */
.article-meta {
  display: flex;
  gap: 18px;
  font-size: 12.5px;
  color: #718096;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-icon {
  color: #94a3b8;
}

/* 无热门文章提示 */
.no-hot-articles {
  padding: 50px 20px;
  text-align: center;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .content-wrapper {
    flex-direction: column;
  }

  .hot-article-card {
    margin-top: 30px;
    padding: 16px;
  }

  .search-card {
    padding: 16px;
  }

  .rank-icon {
    width: 24px;
    height: 24px;
    line-height: 24px;
    font-size: 14px;
  }

  .article-title {
    font-size: 14.5px;
  }
}
</style>
