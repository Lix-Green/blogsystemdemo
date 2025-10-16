<template>
  <div class="collections-page" v-loading="loading">
    <div class="header">
      <h2>我的收藏</h2>
      <el-button type="primary" @click="refresh">刷新</el-button>
    </div>
    <el-empty v-if="!loading && articles.length === 0" description="暂无收藏"/>
    <div v-else class="grid">
      <ArticleCard
          v-for="item in articles"
          :key="item.id"
          :article="item"
          @click.native="$router.push(`/articles/${item.id}`)"
      />
    </div>
    <div class="pager" v-if="total > 0">
      <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          @current-change="onPageChange"
      />
    </div>
  </div>

</template>
<script setup>
import {ref, onMounted} from 'vue';
import {ElMessage} from 'element-plus';
import {getMyCollectedArticleIds, getArticleDetail} from '../api/article';
import ArticleCard from '../components/ArticleCard.vue';

const loading = ref(false);
const articles = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const fetchData = async () => {
  try {
    loading.value = true;
    // 1) 获取收藏的文章ID列表
    const idsRes = await getMyCollectedArticleIds();
    if (idsRes.code !== 200) {
      ElMessage.error(idsRes.message || '获取收藏列表失败');
      return;
    }
    const ids = idsRes.data || [];
    total.value = ids.length;
    // 2) 前端分页 + 再按ID批量获取详情（简版：多次调用 getArticleDetail 可能较慢，这里复用列表接口做过滤）
    const start = (pageNum.value - 1) * pageSize.value;
    const pageIds = ids.slice(start, start + pageSize.value);
    if (pageIds.length === 0) {
      articles.value = [];
      return;
    }
    // 逐个获取详情，避免列表接口数据结构不一致
    const detailResults = await Promise.all(pageIds.map(id => getArticleDetail(id)));
    const okItems = detailResults
        .filter(r => r && r.code === 200 && r.data)
        .map(r => r.data);
    articles.value = okItems;
  } catch (e) {
    console.error(e);
    ElMessage.error('加载失败');
  } finally {
    loading.value = false;
  }
};
const onPageChange = (p) => {
  pageNum.value = p;
  fetchData();
};
const refresh = () => fetchData();
onMounted(fetchData);
</script>

<style scoped>
.collections-page {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
