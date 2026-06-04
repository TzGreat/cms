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
    <!-- Header -->
    <header class="page-header">
      <div class="header-content">
        <h1 class="page-title">文章列表</h1>
        <p class="page-subtitle">共收录 {{ total }} 篇文章，记录每一个灵感瞬间</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="handleAdd">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" x2="12" y1="5" y2="19"></line>
            <line x1="5" x2="19" y1="12" y2="12"></line>
          </svg>
          <span>添加文章</span>
        </button>
        <button class="btn btn-outline" @click="handleCrawl">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
            <path d="M2 17l10 5 10-5"></path>
            <path d="M2 12l10 5 10-5"></path>
          </svg>
          <span>内容采集</span>
        </button>
      </div>
    </header>

    <!-- Filters -->
    <div class="filter-bar">
      <div class="search-wrapper">
        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
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

    <!-- Loading -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <span class="loading-label">正在加载文章...</span>
    </div>

    <!-- Empty -->
    <div v-else-if="filteredArticles.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"></path>
          <polyline points="14 2 14 8 20 8"></polyline>
          <line x1="16" x2="8" y1="13" y2="13"></line>
          <line x1="16" x2="8" y1="17" y2="17"></line>
          <polyline points="10 9 9 9 8 9"></polyline>
        </svg>
      </div>
      <h3 class="empty-title">暂无文章</h3>
      <p class="empty-desc">点击上方按钮，开始撰写你的第一篇文章</p>
    </div>

    <!-- Article Grid -->
    <div v-else class="article-grid stagger-animation">
      <article
        v-for="article in filteredArticles"
        :key="article.id"
        class="article-card"
      >
        <div class="card-badges">
          <span class="badge badge-category">{{ getCategoryName(article.categoryId) }}</span>
          <span :class="['badge badge-state', article.state === 'p' ? 'badge-published' : 'badge-draft']">
            {{ article.state === 'p' ? '已发布' : '草稿' }}
          </span>
        </div>

        <h3 class="card-title" @click="handleView(article.id)">{{ article.title }}</h3>

        <p class="card-excerpt">
          {{ article.summary || article.content.substring(0, 120) }}...
        </p>

        <div class="card-meta">
          <span class="meta-item">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            {{ article.createTime }}
          </span>
          <span class="meta-item">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
            </svg>
            {{ article.content.length }} 字
          </span>
        </div>

        <div class="card-footer">
          <button class="action-btn action-view" @click="handleView(article.id)">
            <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
              <circle cx="12" cy="12" r="3"></circle>
            </svg>
            <span>查看</span>
          </button>
          <button class="action-btn action-edit" @click="handleEdit(article.id)">
            <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
            </svg>
            <span>编辑</span>
          </button>
          <button class="action-btn action-delete" @click="handleDelete(article.id)">
            <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 6h18"></path>
              <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
              <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
            </svg>
            <span>删除</span>
          </button>
        </div>
      </article>
    </div>

    <!-- Pagination -->
    <nav v-if="!loading && total > pageSize" class="pagination">
      <button
        :disabled="pageNum <= 1"
        @click="handlePageChange(pageNum - 1)"
        class="page-btn"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </button>
      <span class="page-info">
        第 <strong>{{ pageNum }}</strong> / {{ Math.ceil(total / pageSize) }} 页
      </span>
      <button
        :disabled="pageNum >= Math.ceil(total / pageSize)"
        @click="handlePageChange(pageNum + 1)"
        class="page-btn"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </button>
    </nav>
  </div>
</template>

<style scoped>
/* ============================================
   ArticleList — Editorial Elegance Design
   ============================================ */

.article-list {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  padding: 36px 32px;
  max-width: 100%;
}

/* ---------- Page Header ---------- */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 28px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-light);
}

.page-title {
  font-family: var(--font-display);
  font-size: 1.75rem;
  font-weight: 400;
  color: var(--text-primary);
  margin: 0 0 6px;
  letter-spacing: -0.01em;
}

.page-subtitle {
  font-family: var(--font-body);
  font-size: 0.875rem;
  color: var(--text-muted);
  margin: 0;
  letter-spacing: 0.01em;
}

.header-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

/* ---------- Buttons ---------- */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 22px;
  border-radius: var(--radius-md);
  font-family: var(--font-body);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  line-height: 1;
}

.btn-primary {
  background: var(--accent-color);
  color: #fff;
  box-shadow: var(--shadow-xs);
}

.btn-primary:hover {
  background: var(--accent-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.btn-outline {
  background: transparent;
  color: var(--text-secondary);
  border: 1.5px solid var(--border-color);
}

.btn-outline:hover {
  border-color: var(--accent-color);
  color: var(--accent-color);
  background: var(--accent-soft);
}

/* ---------- Filter Bar ---------- */
.filter-bar {
  display: flex;
  gap: 14px;
  margin-bottom: 28px;
  flex-wrap: wrap;
  align-items: center;
}

.search-wrapper {
  flex: 1;
  min-width: 220px;
  max-width: 380px;
  position: relative;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 10px 16px 10px 42px;
  border: 1.5px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-body);
  font-size: 0.875rem;
  color: var(--text-primary);
  background: var(--bg-secondary);
  transition: all 0.25s ease;
  outline: none;
}

.search-input::placeholder {
  color: var(--text-muted);
}

.search-input:focus {
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px var(--accent-soft);
  background: var(--bg-primary);
}

.filter-selects {
  display: flex;
  gap: 10px;
}

.filter-select {
  padding: 10px 36px 10px 14px;
  border: 1.5px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-body);
  font-size: 0.875rem;
  color: var(--text-primary);
  background: var(--bg-secondary);
  cursor: pointer;
  min-width: 120px;
  outline: none;
  transition: all 0.25s ease;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%23999' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
}

.filter-select:focus {
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px var(--accent-soft);
}

/* ---------- Loading State ---------- */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 72px 20px;
  gap: 16px;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--border-light);
  border-top-color: var(--accent-color);
  border-radius: 50%;
  animation: editorial-spin 0.9s cubic-bezier(0.4, 0, 0.2, 1) infinite;
}

@keyframes editorial-spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-label {
  font-family: var(--font-body);
  font-size: 0.875rem;
  color: var(--text-muted);
  letter-spacing: 0.02em;
}

/* ---------- Empty State ---------- */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  gap: 10px;
}

.empty-icon {
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--bg-tertiary);
  color: var(--text-muted);
  margin-bottom: 8px;
}

.empty-title {
  font-family: var(--font-display);
  font-size: 1.25rem;
  font-weight: 400;
  color: var(--text-secondary);
  margin: 0;
}

.empty-desc {
  font-family: var(--font-body);
  font-size: 0.875rem;
  color: var(--text-muted);
  margin: 0;
}

/* ---------- Article Grid ---------- */
.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

/* Stagger animation */
.stagger-animation > .article-card {
  opacity: 0;
  transform: translateY(16px);
  animation: card-enter 0.45s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

.stagger-animation > .article-card:nth-child(1)  { animation-delay: 0.03s; }
.stagger-animation > .article-card:nth-child(2)  { animation-delay: 0.07s; }
.stagger-animation > .article-card:nth-child(3)  { animation-delay: 0.11s; }
.stagger-animation > .article-card:nth-child(4)  { animation-delay: 0.15s; }
.stagger-animation > .article-card:nth-child(5)  { animation-delay: 0.19s; }
.stagger-animation > .article-card:nth-child(6)  { animation-delay: 0.23s; }
.stagger-animation > .article-card:nth-child(7)  { animation-delay: 0.27s; }
.stagger-animation > .article-card:nth-child(8)  { animation-delay: 0.31s; }
.stagger-animation > .article-card:nth-child(9)  { animation-delay: 0.35s; }
.stagger-animation > .article-card:nth-child(10) { animation-delay: 0.39s; }

@keyframes card-enter {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ---------- Article Card ---------- */
.article-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  padding: 22px 22px 18px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-color);
}

/* ---------- Badges ---------- */
.card-badges {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 12px;
  border-radius: 20px;
  font-family: var(--font-body);
  font-size: 0.75rem;
  font-weight: 500;
  letter-spacing: 0.02em;
}

.badge-category {
  background: var(--accent-soft);
  color: var(--accent-color);
}

.badge-state {
  font-size: 0.7rem;
}

.badge-published {
  background: var(--success-soft);
  color: var(--success-color);
}

.badge-draft {
  background: var(--warning-soft);
  color: var(--warning-color);
}

/* ---------- Card Title ---------- */
.card-title {
  font-family: var(--font-display);
  font-size: 1.15rem;
  font-weight: 400;
  color: var(--text-primary);
  margin: 0 0 10px;
  cursor: pointer;
  transition: color 0.2s ease;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-boxorient: vertical;
  overflow: hidden;
}

.card-title:hover {
  color: var(--accent-color);
}

/* ---------- Card Excerpt ---------- */
.card-excerpt {
  font-family: var(--font-body);
  font-size: 0.8125rem;
  color: var(--text-secondary);
  margin: 0 0 16px;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex-grow: 1;
}

/* ---------- Card Meta ---------- */
.card-meta {
  display: flex;
  gap: 18px;
  margin-bottom: 16px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-family: var(--font-body);
  font-size: 0.75rem;
  color: var(--text-muted);
}

/* ---------- Card Footer / Actions ---------- */
.card-footer {
  display: flex;
  gap: 8px;
  padding-top: 14px;
  border-top: 1px solid var(--border-light);
}

.action-btn {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  padding: 7px 10px;
  border-radius: var(--radius-sm);
  font-family: var(--font-body);
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  line-height: 1;
}

.action-view {
  background: var(--success-soft);
  color: var(--success-color);
}

.action-view:hover {
  background: var(--success-color);
  color: #fff;
}

.action-edit {
  background: var(--accent-soft);
  color: var(--accent-color);
}

.action-edit:hover {
  background: var(--accent-color);
  color: #fff;
}

.action-delete {
  background: var(--error-soft);
  color: var(--error-color);
}

.action-delete:hover {
  background: var(--error-color);
  color: #fff;
}

/* ---------- Pagination ---------- */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 36px;
  padding-top: 24px;
  border-top: 1px solid var(--border-light);
}

.page-btn {
  width: 36px;
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  background: var(--accent-color);
  color: #fff;
  border-color: var(--accent-color);
  box-shadow: var(--shadow-xs);
}

.page-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.page-info {
  font-family: var(--font-body);
  font-size: 0.8125rem;
  color: var(--text-muted);
  letter-spacing: 0.01em;
}

.page-info strong {
  color: var(--text-primary);
  font-weight: 600;
}

/* ---------- Responsive ---------- */
@media (max-width: 768px) {
  .article-list {
    padding: 24px 18px;
  }

  .article-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    gap: 18px;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions .btn {
    flex: 1;
    justify-content: center;
  }

  .filter-bar {
    flex-direction: column;
  }

  .search-wrapper {
    max-width: none;
  }

  .filter-selects {
    width: 100%;
  }

  .filter-select {
    flex: 1;
  }
}
</style>
