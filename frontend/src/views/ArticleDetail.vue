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
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 32px;
  box-shadow: var(--shadow-md);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.back-btn {
  padding: 8px 18px;
  background: transparent;
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-family: var(--font-body);
  font-size: 14px;
  transition: all 0.25s ease;
}

.back-btn:hover {
  background: var(--bg-hover);
  border-color: var(--accent-color);
  color: var(--accent-color);
}

.edit-btn {
  padding: 8px 22px;
  background: var(--accent-color);
  color: var(--bg-card);
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.25s ease;
}

.edit-btn:hover {
  background: var(--accent-hover);
}

.title {
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 400;
  color: var(--text-primary);
  margin: 0 0 18px 0;
  line-height: 1.35;
  letter-spacing: -0.01em;
}

.meta {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  align-items: center;
}

.category {
  padding: 4px 14px;
  background: var(--accent-soft);
  color: var(--accent-color);
  border-radius: var(--radius-xl);
  font-size: 13px;
  font-weight: 500;
  font-family: var(--font-body);
}

.state {
  padding: 4px 14px;
  border-radius: var(--radius-xl);
  font-size: 13px;
  font-weight: 500;
  font-family: var(--font-body);
}

.state.d {
  background: var(--warning-soft);
  color: var(--warning-color);
}

.state.p {
  background: var(--success-soft);
  color: var(--success-color);
}

.time {
  color: var(--text-muted);
  font-size: 13px;
  font-family: var(--font-body);
  margin-left: auto;
}

.cover {
  margin-bottom: 28px;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.cover img {
  width: 100%;
  height: auto;
  max-height: 420px;
  object-fit: cover;
  display: block;
}

.summary-section {
  background: var(--bg-tertiary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  padding: 22px;
  margin-bottom: 28px;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.summary-header h3 {
  margin: 0;
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 400;
  color: var(--text-primary);
}

.refresh-btn {
  padding: 6px 16px;
  background: var(--bg-card);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-family: var(--font-body);
  font-size: 13px;
  transition: all 0.25s ease;
}

.refresh-btn:hover:not(:disabled) {
  background: var(--bg-hover);
  border-color: var(--accent-color);
  color: var(--accent-color);
}

.refresh-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.summary-content {
  font-family: var(--font-body);
  font-size: 15px;
  line-height: 1.75;
  color: var(--text-secondary);
  background: var(--bg-inset);
  padding: 14px 18px;
  border-radius: var(--radius-sm);
}

.summary-empty {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--text-muted);
  text-align: center;
  padding: 18px;
  background: var(--bg-inset);
  border-radius: var(--radius-sm);
}

.article-content {
  font-family: var(--font-body);
  font-size: 16px;
  line-height: 1.85;
  color: var(--text-secondary);
  white-space: pre-wrap;
  word-break: break-word;
}

.loading {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-muted);
  font-family: var(--font-body);
  font-size: 15px;
}
</style>
