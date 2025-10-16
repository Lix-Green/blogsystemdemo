<template>
  <div class="article-edit-container">
    <el-row :gutter="20">
      <el-col :xs="24" :md="20" :offset="md ? 2 : 0">
        <div class="page-title">
          {{ id ? '编辑文章' : '新增文章' }}
        </div>

        <div class="form-container" v-loading="loading">
          <ArticleForm
              :article="article"
              @submit="handleFormSubmit"
          />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import {createArticle, getArticleDetail, updateArticle} from '../api/article';
import ArticleForm from '../components/ArticleForm.vue';

// 获取路由参数和路由实例
const route = useRoute();
const router = useRouter();
const {id} = route.params;

// 文章数据
const article = ref({});
// 加载状态
const loading = ref(false);

// 获取文章详情（编辑时）
const fetchArticleDetail = async () => {
  if (!id) return;

  try {
    loading.value = true;
    const res = await getArticleDetail(id);

    // 检查后端返回格式并正确提取数据
    if (res.code === 200 && res.data) {
      article.value = {
        id: res.data.id,
        title: res.data.title || '',
        content: res.data.content || '',
        summary: res.data.summary || '',
        coverImage: res.data.coverImage || '',
        status: res.data.status !== undefined ? res.data.status : 1
      };
    } else {
      ElMessage.error('获取文章详情失败：' + (res.message || '数据格式不正确'));
    }
  } catch (error) {
    ElMessage.error('获取文章详情失败');
    console.error('获取文章详情失败', error);
  } finally {
    loading.value = false;
  }
};

// 处理表单提交（完全适配后端返回格式）
const handleFormSubmit = async (formData) => {
  try {
    loading.value = true;

    if (id) {
      // 编辑文章逻辑
      const res = await updateArticle(id, formData);

      console.log('更新文章响应:', res);

      if (res.code === 200) {
        ElMessage.success('文章更新成功');
        router.push(`/articles/${id}`);
      } else {
        ElMessage.error(`文章更新失败：${res.message || '未知错误'}`);
      }
    } else {
      // 新增文章逻辑（核心修复）
      const res = await createArticle(formData);

      console.log('新增文章响应:', res);

      // 后端返回格式：{code:200, message:"success", data: 文章ID}
      if (res.code === 200) {
        // 确认data是数字类型的ID
        if (typeof res.data === 'number') {
          ElMessage.success('文章创建成功');
          // 跳转到新创建的文章详情页
          setTimeout(() => {
            router.push(`/articles/${res.data}`);
          }, 800);
        } else {
          // 虽然code是200，但data格式不正确
          ElMessage.success('文章创建成功');
          // 跳转到文章列表页
          setTimeout(() => {
            router.push('/articles');
          }, 800);
        }
      } else {
        // 后端明确返回失败
        ElMessage.error(`文章创建失败：${res.message || '未知错误'}`);
      }
    }
  } catch (error) {
    // 捕获网络错误或代码异常
    const operation = id ? '更新' : '创建';
    ElMessage.error(`文章${operation}失败：网络错误或服务器无响应`);
    console.error(`文章${operation}异常：`, error);
  } finally {
    loading.value = false;
  }
};

// 页面加载时获取文章详情（编辑时）
onMounted(() => {
  if (id) {
    fetchArticleDetail();
  }
});
</script>

<style scoped>
.article-edit-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.form-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
</style>
