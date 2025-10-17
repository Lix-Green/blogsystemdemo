<!--
  ArticleForm 文章表单组件
  用于文章的发布和编辑，包含标题、摘要、封面、内容、状态等字段。
  支持表单校验、图片预览、富文本编辑、提交与重置。
-->
<template>
  <el-form
      ref="articleFormRef"
      :model="form"
      label-width="120px"
      :rules="rules"
      @submit.prevent="handleSubmit"
  >
    <el-form-item label="文章标题" prop="title">
      <el-input v-model="form.title" placeholder="请输入文章标题"/>
    </el-form-item>

    <el-form-item label="文章摘要" prop="summary">
      <el-input
          v-model="form.summary"
          placeholder="请输入文章摘要"
          type="textarea"
          rows="2"
      />
    </el-form-item>

    <el-form-item label="封面图片" prop="coverImage">
      <el-input v-model="form.coverImage" placeholder="请输入图片URL"/>
      <el-image
          v-if="form.coverImage"
          :src="form.coverImage"
          style="width: 200px; height: 100px; margin-top: 10px"
          fit="cover"
          @error="handleImageError"
      />
    </el-form-item>

    <el-form-item label="文章内容" prop="content">
      <div class="rich-editor">
        <QuillEditor
            v-model:content="form.content"
            contentType="html"
            theme="snow"
            :readOnly="submitLoading"
            placeholder="请输入文章内容"
        />
      </div>
    </el-form-item>

    <el-form-item label="状态" prop="status">
      <el-select
          v-model="form.status"
          placeholder="请选择状态"
          clearable
      >
        <el-option label="草稿" :value="0"/>
        <el-option label="发布" :value="1"/>
      </el-select>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
        {{ props.article.id ? '更新文章' : '发布文章' }}
      </el-button>
      <el-button @click="handleReset" :disabled="submitLoading">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
// 引入 Vue 相关 API 和第三方组件
import {ref, reactive, watch, defineProps, defineEmits} from 'vue';
import {ElMessage, ElImage} from 'element-plus';
import {QuillEditor} from '@vueup/vue-quill';

/**
 * Props：接收父组件传递的文章数据
 * @property {Object} article 编辑时传入已有文章数据，新增时为空对象
 */
const props = defineProps({
  article: {
    type: Object,
    default: () => ({}) // 编辑时传入已有文章数据，新增时为空对象
  }
});

/**
 * Emits：定义组件事件
 * @event submit 提交表单
 * @event cancel 重置表单
 */
const emit = defineEmits(['submit', 'cancel']);

// 表单引用与加载状态
const articleFormRef = ref(null); // 表单引用
const submitLoading = ref(false); // 提交加载状态（防止重复提交）

/**
 * 表单数据
 */
const form = reactive({
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  status: 1 // 0：草稿，1：发布（与后端状态定义保持一致）
});

/**
 * 表单校验规则
 */
const rules = {
  title: [
    {required: true, message: '请输入文章标题', trigger: 'blur'},
    {max: 100, message: '标题不能超过100个字符', trigger: 'blur'},
    {pattern: /^[^<>]*$/, message: '标题不能包含尖括号（< >）', trigger: 'blur'}
  ],
  content: [
    {required: true, message: '请输入文章内容', trigger: 'change'},
    {min: 20, message: '内容不能少于20个字符', trigger: 'change'}
  ],
  summary: [
    {required: true, message: '请输入文章摘要', trigger: 'blur'},
    {max: 200, message: '摘要不能超过200个字符', trigger: 'blur'}
  ],
  coverImage: [
    {required: true, message: '请输入封面图片URL', trigger: 'blur'},
    {type: 'url', message: '请输入合法的图片URL（以http/https开头）', trigger: 'blur'}
  ],
  status: [
    {required: true, message: '请选择文章状态', trigger: 'change'}
  ]
};

/**
 * 监听 props.article 变化，编辑时自动填充表单
 */
watch(
    () => props.article,
    (newArticle) => {
      if (newArticle && newArticle.id) { // 存在id说明是“编辑”场景
        form.title = newArticle.title || '';
        form.content = newArticle.content || '';
        form.summary = newArticle.summary || '';
        form.coverImage = newArticle.coverImage || '';
        // 关键：确保status为数字类型（避免后端接收字符串类型错误）
        form.status = newArticle.status !== undefined ? Number(newArticle.status) : 1;
      } else { // 新增场景：重置表单默认值
        form.status = 1;
      }
    },
    {immediate: true} // 组件初始化时立即执行（编辑场景自动填充）
);

/**
 * 图片加载错误处理，加载失败时显示默认图片
 */
const handleImageError = (e) => {
  // 图片加载失败时显示默认图（需提前在assets目录放置默认图片）
  e.target.src = '/src/assets/images/default-cover.jpg';
};

/**
 * 表单提交逻辑，校验并提交数据
 */
const handleSubmit = async () => {
  try {
    // 7.1 表单验证
    await articleFormRef.value.validate();

    // 7.2 开启加载状态（防止重复点击）
    submitLoading.value = true;

    // 7.3 提交数据（若为编辑，携带文章id；新增则不携带）
    const submitData = {
      ...form,
      id: props.article.id || undefined // 编辑时传入id，新增时不传递id
    };

    // 7.4 触发父组件提交事件（父组件调用api发送请求）
    emit('submit', submitData);

    // 7.5 提交成功提示（若父组件不处理提示，可在此添加）
    // ElMessage.success(props.article.id ? '文章更新成功' : '文章发布成功');

  } catch (error) {
    // 7.6 验证失败处理
    ElMessage.error('请完善表单信息后再提交');
    console.error('表单验证失败：', error);
  } finally {
    // 7.7 关闭加载状态（无论成功失败都执行）
    submitLoading.value = false;
  }
};

/**
 * 表单重置逻辑，恢复初始状态
 */
const handleReset = () => {
  if (articleFormRef.value) {
    articleFormRef.value.resetFields();
    // 重置后恢复状态默认值（避免重置为undefined）
    form.status = props.article.id ? Number(props.article.status) : 1;
  }
  // 触发父组件取消事件（可选，如关闭弹窗）
  emit('cancel');
};
</script>

<style scoped>
/* 优化表单样式，提升视觉体验 */
.el-form-item {
  margin-bottom: 20px;
}

.el-input__inner, .el-textarea__inner, .el-select__inner {
  border-radius: 4px;
}

.el-image {
  border-radius: 4px;
  border: 1px solid #eee;
}

.el-button + .el-button {
  margin-left: 12px;
}

/* 富文本容器：与 Element Plus 风格更协调 */
.rich-editor {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  overflow: hidden;
}

.rich-editor :deep(.ql-toolbar) {
  background: #fafafa;
  border: none;
  border-bottom: 1px solid #ebeef5;
}

.rich-editor :deep(.ql-container) {
  min-height: 420px;
  border: none;
}

.rich-editor :deep(.ql-editor) {
  min-height: 380px;
  font-size: 14px;
  line-height: 1.8;
  padding: 16px;
}
</style>
