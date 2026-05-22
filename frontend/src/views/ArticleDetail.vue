<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { articleApi, categoryApi, aiApi, type Article, type Category } from '@/api'

const router = useRouter()
const route = useRoute()
const article = ref<Article | null>(null)
const category = ref<Category | null>(null)
const isRefreshing = ref(false)

const fetchArticle = async () => {
  const id = parseInt(route.params.id as string)
  if (!id) return

  try {
    const result = await articleApi.getById(id)
    if (result.code === 0) {
      article.value = result.data
      // 获取分类信息
      if (article.value.categoryId) {
        await fetchCategory(article.value.categoryId)
      }
    }
  } catch (error) {
    console.error('获取文章详情失败', error)
  }
}

const fetchCategory = async (categoryId: number) => {
  try {
    const result = await categoryApi.getById(categoryId)
    if (result.code === 0) {
      category.value = result.data
    }
  } catch (error) {
    console.error('获取分类信息失败', error)
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
      alert('刷新摘要失败: ' + result.message)
    }
  } catch (error) {
    console.error('刷新摘要失败', error)
    alert('刷新摘要失败')
  } finally {
    isRefreshing.value = false
  }
}

const handleEdit = () => {
  if (article.value) {
    router.push(`/article/edit/${article.value.id}`)
  }
}

const handleBack = () => {
  router.push('/home/articles')
}

onMounted(() => {
  fetchArticle()
})
</script>

<template>
  <div class="article-detail">
    <div class="header">
      <button class="back-btn" @click="handleBack">← 返回列表</button>
      <button class="edit-btn" @click="handleEdit">编辑文章</button>
    </div>

    <div v-if="article" class="content">
      <h1 class="title">{{ article.title }}</h1>

      <div class="meta">
        <span class="category" v-if="category">{{ category.categoryName }}</span>
        <span class="state" :class="article.state">
          {{ article.state === 'd' ? '草稿' : '已发布' }}
        </span>
        <span class="time">{{ article.createTime }}</span>
      </div>

      <!-- 文章摘要 -->
      <div class="summary-section">
        <div class="summary-header">
          <h3>📝 文章摘要</h3>
          <button 
            class="refresh-btn" 
            @click="handleRefreshSummary" 
            :disabled="isRefreshing"
          >
            {{ isRefreshing ? '生成中...' : (article.summary ? '🔄 刷新摘要' : '✨ 生成摘要') }}
          </button>
        </div>
        <div v-if="article.summary" class="summary-content">
          {{ article.summary }}
        </div>
        <div v-else class="summary-empty">
          暂无摘要，点击上方按钮生成
        </div>
      </div>

      <div v-if="article.coverImg" class="cover">
        <img :src="article.coverImg" :alt="article.title" />
      </div>

      <div class="article-content">
        {{ article.content }}
      </div>
    </div>

    <div v-else class="loading">加载中...</div>
  </div>
</template>

<style scoped>
.article-detail {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  padding: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.back-btn {
  padding: 8px 16px;
  background: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s;
}

.back-btn:hover {
  background: #e9ecef;
}

.edit-btn {
  padding: 8px 20px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s;
}

.edit-btn:hover {
  background: #2980b9;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
  line-height: 1.4;
}

.meta {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.category {
  padding: 4px 12px;
  background: #3498db;
  color: white;
  border-radius: 20px;
  font-size: 14px;
}

.state {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
}

.state.d {
  background: #f1c40f;
  color: #fff;
}

.state.p {
  background: #27ae60;
  color: #fff;
}

.time {
  color: #999;
  font-size: 14px;
}

.cover {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
}

.cover img {
  width: 100%;
  height: auto;
  max-height: 400px;
  object-fit: cover;
}

.summary-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  color: white;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.summary-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.refresh-btn {
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.refresh-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.summary-content {
  font-size: 15px;
  line-height: 1.7;
  opacity: 0.95;
  background: rgba(255, 255, 255, 0.1);
  padding: 12px 16px;
  border-radius: 8px;
}

.summary-empty {
  font-size: 14px;
  opacity: 0.7;
  text-align: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 8px;
}

.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: #444;
  white-space: pre-wrap;
  word-break: break-word;
}

.loading {
  text-align: center;
  padding: 60px;
  color: #999;
}
</style>