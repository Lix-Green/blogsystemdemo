<!--
  ArticleList 文章列表页面
  展示所有文章，支持新增、编辑、删除等操作。
-->
<template>
  <div class="article-list-container">
    <div class="page-header">
      <h2>文章管理</h2>
      <el-button type="primary" @click="$router.push('/articles/edit')">
        <Plus/>
        新增文章
      </el-button>
    </div>

    <el-table
        :data="articles"
        border
        style="width: 100%; margin-top: 20px"
        v-loading="loading"
    >
      <el-table-column prop="id" label="ID" width="80"/>
      <!-- 关键修复：添加 :formatter 和 :cell-style，并使用 v-html 显示 -->
      <el-table-column
          label="标题"
          min-width="200"
      >
        <template #default="scope">
          <div v-html="formatTitle(scope.row)"></div>
        </template>
      </el-table-column>
      <el-table-column prop="summary" label="摘要" min-width="200"/>
      <el-table-column
          prop="createdTime"
          label="创建时间"
          width="180"
          :formatter="formatTime"
      />
      <el-table-column
          prop="status"
          label="状态"
          width="100"
          :formatter="formatStatus"
      />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button
              type="text"
              size="small"
              @click="$router.push(`/articles/edit/${scope.row.id}`)"
          >
            编辑
          </el-button>
          <el-button
              type="text"
              size="small"
              text-color="#ff4d4f"
              @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination" style="margin-top: 20px; text-align: right">
      <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {getMyArticles, deleteArticle} from '../api/article';
import {Plus} from '@element-plus/icons-vue';

/**
 * articles 文章列表数据
 * loading 加载状态
 */
// 文章列表数据
const articles = ref([]);
// 加载状态
const loading = ref(false);
// 分页参数
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

/**
 * 获取文章列表
 */
// 获取文章列表
const fetchArticles = async () => {
  try {
    loading.value = true;
    const res = await getMyArticles({pageNum: pageNum.value, pageSize: pageSize.value});

    // 校验后端返回的基础结构
    if (res.code !== 200) {
      ElMessage.error(`获取失败：${res.message || '未知错误'}`);
      articles.value = [];
      total.value = 0;
      return;
    }

    // 解析文章列表：从 res.data.articles 中获取
    const articleData = res.data || {};
    articles.value = Array.isArray(articleData.articles) ? articleData.articles : [];
    total.value = articleData.total || 0;

    // 无数据提示
    if (articles.value.length === 0) {
      ElMessage.info('当前暂无文章数据');
    }

  } catch (error) {
    console.error('获取文章列表失败', error);
    articles.value = [];
    total.value = 0;
    ElMessage.error('获取文章列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

/**
 * 删除文章
 */
// 删除文章
const handleDelete = async (id) => {
  ElMessageBox.confirm(
      '确定要删除这篇文章吗？此操作不可撤销。',
      '确认删除',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      await deleteArticle(id);
      ElMessage.success('删除成功');
      // 重新获取文章列表
      fetchArticles();
    } catch (error) {
      ElMessage.error('删除失败，请稍后重试');
      console.error('删除文章失败', error);
    }
  }).catch(() => {
    // 取消删除
  });
};

/**
 * 格式化标题，支持高亮或HTML展示
 * @param {Object} row 文章数据
 * @returns {string}
 */
// 格式化标题（添加链接）
const formatTitle = (row) => {
  return `<a href="#/articles/${row.id}" style="color: #409eff;">${row.title}</a>`;
};

/**
 * 格式化时间
 * @param {string} time 时间字符串
 * @returns {string}
 */
// 格式化时间
const formatTime = (row) => {
  if (!row.createdTime) return '';
  return new Date(row.createdTime).toLocaleString();
};

/**
 * 格式化状态
 * @param {number} status 状态码
 * @returns {string}
 */
// 格式化状态
const formatStatus = (row) => {
  return row.status === 1 ? '已发布' : row.status === 0 ? '草稿' : '未知';
};

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchArticles();
};

// 当前页变化
const handleCurrentChange = (val) => {
  pageNum.value = val;
  fetchArticles();
};

// 页面加载时获取文章列表
onMounted(() => {
  fetchArticles();
});
</script>

<style scoped>
.article-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

/* 确保标题链接样式正确 */
a {
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}
</style>
