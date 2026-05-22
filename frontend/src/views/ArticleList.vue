<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { articleApi, categoryApi, type Article, type Category, type PageBean } from '@/api'

const router = useRouter()
const articles = ref<Article[]>([])
const categories = ref<Category[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const categoryId = ref<number | undefined>()
const state = ref<string>('')
const loading = ref(false)
const searchQuery = ref('')

const filteredArticles = computed(() => {
  if (!searchQuery.value) return articles.value
  const query = searchQuery.value.toLowerCase()
  return articles.value.filter(article => 
    article.title.toLowerCase().includes(query) ||
    article.content.toLowerCase().includes(query)
  )
})

const fetchArticles = async () => {
  loading.value = true
  try {
    const result = await articleApi.list(pageNum.value, pageSize.value, categoryId.value || undefined, state.value || undefined)
    if (result.code === 0) {
      const data = result.data as PageBean<Article>
      articles.value = data.items
      total.value = data.total
    }
  } catch (error) {
    console.error('获取文章列表失败', error)
  } finally {
    loading.value = false
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

const handleAdd = () => {
  router.push('/article/add')
}

const handleCrawl = () => {
  router.push('/article/crawl')
}

const handleView = (id: number) => {
  router.push(`/article/detail/${id}`)
}

const handleEdit = (id: number) => {
  router.push(`/article/edit/${id}`)
}

const handleDelete = async (id: number) => {
  if (!confirm('确定要删除这篇文章吗？')) return
  try {
    const result = await articleApi.delete(id)
    if (result.code === 0) {
      fetchArticles()
    }
  } catch (error) {
    console.error('删除文章失败', error)
  }
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchArticles()
}

const handleFilter = () => {
  pageNum.value = 1
  fetchArticles()
}

const getCategoryName = (catId: number) => {
  return categories.value.find(c => c.id === catId)?.categoryName || '-'
}

onMounted(() => {
  fetchArticles()
  fetchCategories()
})
</script>

<template>
  <div class="article-list">
    <div class="header-section">
      <div class="header-left">
        <h2 class="section-title">文章列表</h2>
        <p class="section-subtitle">共 {{ total }} 篇文章</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="handleAdd">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" x2="12" y1="5" y2="19"></line>
            <line x1="5" x2="19" y1="12" y2="12"></line>
          </svg>
          <span>添加文章</span>
        </button>
        <button class="btn btn-secondary" @click="handleCrawl">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M19 11H5"></path>
            <path d="M12 19V5"></path>
          </svg>
          <span>内容采集</span>
        </button>
      </div>
    </div>

    <div class="filter-section">
      <div class="search-box">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" x2="16.65" y1="21" y2="16.65"></line>
        </svg>
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="搜索文章标题或内容..." 
          class="search-input"
        />
      </div>
      <div class="filter-selects">
        <select v-model="categoryId" @change="handleFilter" class="filter-select">
          <option :value="undefined">全部分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.categoryName }}
          </option>
        </select>
        <select v-model="state" @change="handleFilter" class="filter-select">
          <option value="">全部状态</option>
          <option value="d">草稿</option>
          <option value="p">已发布</option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <span class="loading-text">加载中...</span>
    </div>

    <div v-else-if="filteredArticles.length === 0" class="empty-state">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"></path>
        <polyline points="14 2 14 8 20 8"></polyline>
        <line x1="16" x2="8" y1="13" y2="13"></line>
        <line x1="16" x2="8" y1="17" y2="17"></line>
        <polyline points="10 9 9 9 8 9"></polyline>
      </svg>
      <h3>暂无文章</h3>
      <p>点击上方按钮添加第一篇文章</p>
    </div>

    <div v-else class="article-grid">
      <div 
        v-for="article in filteredArticles" 
        :key="article.id" 
        class="article-card"
      >
        <div class="card-header">
          <div class="category-badge">
            {{ getCategoryName(article.categoryId) }}
          </div>
          <span :class="['state-badge', article.state === 'p' ? 'published' : 'draft']">
            {{ article.state === 'p' ? '已发布' : '草稿' }}
          </span>
        </div>
        <h3 class="card-title" @click="handleView(article.id)">{{ article.title }}</h3>
        <p class="card-summary">{{ article.summary || article.content.substring(0, 100) }}...</p>
        <div class="card-meta">
          <span class="meta-item">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            {{ article.createTime }}
          </span>
          <span class="meta-item">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
            </svg>
            {{ article.content.length }} 字
          </span>
        </div>
        <div class="card-actions">
          <button class="action-btn view" @click="handleView(article.id)">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
            </svg>
            <span>查看</span>
          </button>
          <button class="action-btn edit" @click="handleEdit(article.id)">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
            </svg>
            <span>编辑</span>
          </button>
          <button class="action-btn delete" @click="handleDelete(article.id)">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 6h18"></path>
              <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
              <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
            </svg>
            <span>删除</span>
          </button>
        </div>
      </div>
    </div>

    <div v-if="!loading && total > pageSize" class="pagination">
      <button 
        :disabled="pageNum <= 1" 
        @click="handlePageChange(pageNum - 1)"
        class="pagination-btn"
      >
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </button>
      <span class="pagination-info">
        第 <strong>{{ pageNum }}</strong> / {{ Math.ceil(total / pageSize) }} 页
      </span>
      <button 
        :disabled="pageNum >= Math.ceil(total / pageSize)" 
        @click="handlePageChange(pageNum + 1)"
        class="pagination-btn"
      >
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.article-list {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  padding: 28px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-color);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.section-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.section-subtitle {
  font-size: 0.875rem;
  color: var(--text-muted);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn svg {
  width: 18px;
  height: 18px;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

.btn-secondary {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.3);
}

.btn-secondary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
}

.filter-section {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.search-box {
  flex: 1;
  min-width: 200px;
  max-width: 400px;
  position: relative;
}

.search-box svg {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--text-muted);
}

.search-input {
  width: 100%;
  padding: 12px 16px 12px 48px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  transition: all 0.2s ease;
}

.search-input:focus {
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-selects {
  display: flex;
  gap: 12px;
}

.filter-select {
  padding: 12px 16px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  cursor: pointer;
  min-width: 120px;
}

.filter-select:focus {
  border-color: var(--accent-color);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 12px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--border-color);
  border-top-color: var(--accent-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  color: var(--text-muted);
  font-size: 0.9rem;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  gap: 12px;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  color: var(--text-muted);
}

.empty-state h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text-secondary);
  margin: 0;
}

.empty-state p {
  font-size: 0.9rem;
  color: var(--text-muted);
  margin: 0;
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.article-card {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 20px;
  transition: all 0.3s ease;
  border: 1px solid var(--border-light);
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-color);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.category-badge {
  padding: 4px 12px;
  background: var(--accent-color);
  color: white;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
}

.state-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
}

.state-badge.published {
  background: rgba(34, 197, 94, 0.15);
  color: #22c55e;
}

.state-badge.draft {
  background: rgba(245, 158, 11, 0.15);
  color: #f59e0b;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 10px;
  cursor: pointer;
  transition: color 0.2s ease;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-title:hover {
  color: var(--accent-color);
}

.card-summary {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin: 0 0 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  color: var(--text-muted);
}

.meta-item svg {
  width: 14px;
  height: 14px;
}

.card-actions {
  display: flex;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px solid var(--border-light);
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.action-btn svg {
  width: 14px;
  height: 14px;
}

.action-btn.view {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.action-btn.view:hover {
  background: rgba(34, 197, 94, 0.2);
}

.action-btn.edit {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.action-btn.edit:hover {
  background: rgba(59, 130, 246, 0.2);
}

.action-btn.delete {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.2);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.pagination-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}

.pagination-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.pagination-btn svg {
  width: 18px;
  height: 18px;
}

.pagination-info {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.pagination-info strong {
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .article-grid {
    grid-template-columns: 1fr;
  }
  
  .header-section {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: center;
  }
  
  .filter-section {
    flex-direction: column;
  }
  
  .search-box {
    max-width: none;
  }
}
</style>