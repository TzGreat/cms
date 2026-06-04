<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { articleApi, categoryApi, type Article, type Category } from '@/api'

const router = useRouter()
const route = useRoute()
const article = ref<Article | null>(null)
const categories = ref<Category[]>([])
const errorMsg = ref('')

const fetchArticle = async () => {
  const id = parseInt(route.params.id as string)
  if (!id) return

  try {
    const result = await articleApi.getById(id)
    if (result.code === 0) {
      article.value = result.data
    }
  } catch (error) {
    console.error('获取文章详情失败', error)
  }
}

const fetchCategories = async () => {
  try {
    const result = await categoryApi.list()
    if (result.code === 0) {
      categories.value = result.data
    }
  } catch (error) {
    console.error('获取分类列表失败', error)
  }
}

const handleSubmit = async () => {
  if (!article.value) return
  if (!article.value.title || !article.value.content || !article.value.coverImg || !article.value.categoryId) {
    errorMsg.value = '请填写所有必填字段'
    return
  }

  try {
    const result = await articleApi.update(article.value)
    if (result.code === 0) {
      router.push('/home/articles')
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '更新失败'
  }
}

const handleCancel = () => {
  router.push('/home/articles')
}

onMounted(() => {
  fetchArticle()
  fetchCategories()
})
</script>

<template>
  <div class="edit-article">
    <div class="page-header">
      <h2 class="page-title">编辑文章</h2>
      <div class="title-accent"></div>
    </div>

    <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

    <div v-if="article" class="form">
      <div class="form-group">
        <label>标题</label>
        <input v-model="article.title" type="text" placeholder="请输入文章标题" />
      </div>

      <div class="form-group">
        <label>内容</label>
        <textarea v-model="article.content" rows="6" placeholder="请输入文章内容"></textarea>
      </div>

      <div class="form-group">
        <label>封面图片URL</label>
        <input v-model="article.coverImg" type="text" placeholder="请输入封面图片URL" />
      </div>

      <div class="form-group">
        <label>分类</label>
        <select v-model="article.categoryId">
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.categoryName }}
          </option>
        </select>
      </div>

      <div class="form-group">
        <label>状态</label>
        <select v-model="article.state">
          <option value="d">草稿</option>
          <option value="p">已发布</option>
        </select>
      </div>

      <div class="btn-group">
        <button class="submit-btn" @click="handleSubmit">保存</button>
        <button class="cancel-btn" @click="handleCancel">取消</button>
      </div>
    </div>

    <div v-else class="loading">加载中...</div>
  </div>
</template>

<style scoped>
.edit-article {
  --bg-primary: #FAF8F5;
  --bg-secondary: #F2EFEB;
  --bg-tertiary: #EAE6E1;
  --bg-card: #FFFFFF;
  --bg-hover: #F7F5F2;
  --bg-inset: #F5F2EE;
  --text-primary: #1A1A1A;
  --text-secondary: #4A4A4A;
  --text-muted: #8A8580;
  --accent-color: #C45D3E;
  --accent-hover: #A84D33;
  --accent-soft: rgba(196, 93, 62, 0.08);
  --accent-muted: rgba(196, 93, 62, 0.5);
  --success-color: #3A8A5C;
  --success-soft: rgba(58, 138, 92, 0.08);
  --warning-color: #C4883E;
  --warning-soft: rgba(196, 136, 62, 0.08);
  --error-color: #C43E3E;
  --error-soft: rgba(196, 62, 62, 0.07);
  --border-color: #E0DCD7;
  --border-light: #EDEAE6;
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.04);
  --shadow-md: 0 4px 12px rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 8px 30px rgba(0, 0, 0, 0.08);
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
  --radius-xl: 24px;
  --font-display: 'DM Serif Display', Georgia, serif;
  --font-body: 'DM Sans', -apple-system, BlinkMacSystemFont, sans-serif;

  max-width: 680px;
  margin: 0 auto;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  padding: 40px 44px;
  box-shadow: var(--shadow-md);
  font-family: var(--font-body);
  color: var(--text-primary);
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 400;
  color: var(--text-primary);
  margin: 0 0 12px 0;
  letter-spacing: -0.02em;
}

.title-accent {
  width: 48px;
  height: 3px;
  background: var(--accent-color);
  border-radius: 2px;
}

.error-msg {
  background: var(--error-soft);
  color: var(--error-color);
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  margin-bottom: 24px;
  border-left: 3px solid var(--error-color);
  line-height: 1.5;
}

.loading {
  text-align: center;
  padding: 48px 0;
  color: var(--text-muted);
  font-size: 14px;
  letter-spacing: 0.02em;
}

.form-group {
  margin-bottom: 22px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--text-muted);
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 11px 14px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-family: var(--font-body);
  color: var(--text-primary);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px var(--accent-soft);
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: var(--text-muted);
}

.form-group textarea {
  resize: vertical;
  min-height: 140px;
}

.form-group select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%238A8580' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
  padding-right: 36px;
  cursor: pointer;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--border-light);
}

.submit-btn {
  padding: 11px 36px;
  background: var(--accent-color);
  color: var(--bg-card);
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  font-family: var(--font-body);
  font-weight: 600;
  letter-spacing: 0.02em;
  transition: background 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
}

.submit-btn:hover {
  background: var(--accent-hover);
  box-shadow: var(--shadow-sm);
}

.submit-btn:active {
  transform: scale(0.98);
}

.cancel-btn {
  padding: 11px 36px;
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  font-family: var(--font-body);
  font-weight: 500;
  transition: background 0.2s ease, border-color 0.2s ease;
}

.cancel-btn:hover {
  background: var(--bg-hover);
  border-color: var(--text-muted);
}
</style>
