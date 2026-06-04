<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { categoryApi, type Category } from '@/api'

const router = useRouter()
const categories = ref<Category[]>([])
const loading = ref(false)

const fetchCategories = async () => {
  loading.value = true
  try {
    const result = await categoryApi.list()
    if (result.code === 0) {
      categories.value = result.data
    }
  } catch (error) {
    console.error('获取分类列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  router.push('/category/add')
}

const handleEdit = (id: number) => {
  router.push(`/category/edit/${id}`)
}

const handleDelete = async (id: number) => {
  if (!confirm('确定要删除这个分类吗？')) return
  try {
    const result = await categoryApi.delete(id)
    if (result.code === 0) {
      fetchCategories()
    }
  } catch (error) {
    console.error('删除分类失败', error)
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="category-list">
    <div class="header-section">
      <div class="header-left">
        <h2 class="section-title">分类管理</h2>
        <p class="section-subtitle">共 {{ categories.length }} 个分类</p>
      </div>
      <button class="btn btn-primary" @click="handleAdd">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="12" x2="12" y1="5" y2="19"></line>
          <line x1="5" x2="19" y1="12" y2="12"></line>
        </svg>
        <span>添加分类</span>
      </button>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <span class="loading-text">加载中...</span>
    </div>

    <div v-else-if="categories.length === 0" class="empty-state">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
        <circle cx="12" cy="10" r="3"></circle>
      </svg>
      <h3>暂无分类</h3>
      <p>点击上方按钮添加第一个分类</p>
    </div>

    <div v-else class="category-grid">
      <div
        v-for="category in categories"
        :key="category.id"
        class="category-card"
      >
        <div class="card-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            <circle cx="12" cy="10" r="3"></circle>
          </svg>
        </div>
        <h3 class="card-title">{{ category.categoryName }}</h3>
        <p class="card-alias">{{ category.categoryAlias }}</p>
        <div class="card-meta">
          <span class="meta-item">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            {{ category.createTime }}
          </span>
        </div>
        <div class="card-actions">
          <button class="action-btn edit" @click="handleEdit(category.id)">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
            </svg>
            <span>编辑</span>
          </button>
          <button class="action-btn delete" @click="handleDelete(category.id)">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 6h18"></path>
              <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
              <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
            </svg>
            <span>删除</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.category-list {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  padding: 32px;
}

/* ── Header ── */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-light);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.section-title {
  font-family: var(--font-display);
  font-size: 1.6rem;
  font-weight: 400;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -0.01em;
}

.section-subtitle {
  font-family: var(--font-body);
  font-size: 0.85rem;
  color: var(--text-muted);
  margin: 0;
  letter-spacing: 0.02em;
}

/* ── Buttons ── */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 22px;
  border-radius: var(--radius-md);
  font-family: var(--font-body);
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
  border: none;
  letter-spacing: 0.01em;
}

.btn svg {
  width: 16px;
  height: 16px;
}

.btn-primary {
  background: var(--accent-color);
  color: #fff;
  box-shadow: var(--shadow-sm);
}

.btn-primary:hover {
  background: var(--accent-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* ── Loading ── */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 72px 20px;
  gap: 14px;
}

.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--border-light);
  border-top-color: var(--accent-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-family: var(--font-body);
  color: var(--text-muted);
  font-size: 0.875rem;
  letter-spacing: 0.02em;
}

/* ── Empty State ── */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 88px 20px;
  gap: 10px;
}

.empty-state svg {
  width: 56px;
  height: 56px;
  color: var(--accent-muted);
  opacity: 0.6;
}

.empty-state h3 {
  font-family: var(--font-display);
  font-size: 1.2rem;
  font-weight: 400;
  color: var(--text-secondary);
  margin: 0;
}

.empty-state p {
  font-family: var(--font-body);
  font-size: 0.875rem;
  color: var(--text-muted);
  margin: 0;
}

/* ── Grid ── */
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

/* ── Card ── */
.category-card {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 24px;
  transition: all 0.3s ease;
  border: 1px solid var(--border-light);
}

.category-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-color);
}

.card-icon {
  width: 44px;
  height: 44px;
  background: var(--accent-soft);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  color: var(--accent-color);
}

.card-icon svg {
  width: 22px;
  height: 22px;
}

.card-title {
  font-family: var(--font-display);
  font-size: 1.1rem;
  font-weight: 400;
  color: var(--text-primary);
  margin: 0 0 4px;
}

.card-alias {
  font-family: var(--font-body);
  font-size: 0.825rem;
  color: var(--text-muted);
  margin: 0 0 16px;
  font-style: italic;
}

.card-meta {
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: var(--font-body);
  font-size: 0.75rem;
  color: var(--text-muted);
  letter-spacing: 0.01em;
}

.meta-item svg {
  width: 13px;
  height: 13px;
}

/* ── Card Actions ── */
.card-actions {
  display: flex;
  gap: 10px;
  padding-top: 16px;
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
  font-family: var(--font-body);
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  letter-spacing: 0.02em;
}

.action-btn svg {
  width: 14px;
  height: 14px;
}

.action-btn.edit {
  background: var(--accent-soft);
  color: var(--accent-color);
}

.action-btn.edit:hover {
  background: var(--accent-color);
  color: #fff;
}

.action-btn.delete {
  background: var(--error-soft);
  color: var(--error-color);
}

.action-btn.delete:hover {
  background: var(--error-color);
  color: #fff;
}

/* ── Responsive ── */
@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: 1fr;
  }

  .header-section {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
}
</style>
