<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { articleApi, categoryApi, aiApi, type Article, type Category } from '@/api'

const router = useRouter()
const route = useRoute()
const article = ref<Article | null>(null)
const categories = ref<Category[]>([])
const errorMsg = ref('')
const isRefreshing = ref(false)

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

const handleRefreshSummary = async () => {
  if (!article.value || isRefreshing.value) return
  
  isRefreshing.value = true
  try {
    const result = await aiApi.refreshSummary(article.value.id)
    if (result.code === 0) {
      article.value.summary = result.data
    } else {
      errorMsg.value = '刷新摘要失败: ' + result.message
    }
  } catch (error) {
    console.error('刷新摘要失败', error)
    errorMsg.value = '刷新摘要失败'
  } finally {
    isRefreshing.value = false
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
    <h2>编辑文章</h2>
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
  max-width: 600px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  padding: 24px;
}

.edit-article h2 {
  font-size: 20px;
  margin-bottom: 20px;
}

.error-msg {
  color: #e74c3c;
  margin-bottom: 15px;
}

.loading {
  text-align: center;
  padding: 40px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.submit-btn {
  padding: 10px 32px;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.cancel-btn {
  padding: 10px 32px;
  background: #95a5a6;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>