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
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
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
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            <circle cx="12" cy="10" r="3"></circle>
          </svg>
        </div>
        <h3 class="card-title">{{ category.categoryName }}</h3>
        <p class="card-alias">{{ category.categoryAlias }}</p>
        <div class="card-meta">
          <span class="meta-item">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            {{ category.createTime }}
          </span>
        </div>
        <div class="card-actions">
          <button class="action-btn edit" @click="handleEdit(category.id)">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
            </svg>
            <span>编辑</span>
          </button>
          <button class="action-btn delete" @click="handleDelete(category.id)">
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
  </div>
</template>

<style scoped>
.category-list {
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

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.category-card {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 20px;
  transition: all 0.3s ease;
  border: 1px solid var(--border-light);
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-color);
}

.card-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--accent-color) 0%, #8b5cf6 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 14px;
  color: white;
}

.card-icon svg {
  width: 24px;
  height: 24px;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 6px;
}

.card-alias {
  font-size: 0.875rem;
  color: var(--text-muted);
  margin: 0 0 14px;
  font-style: italic;
}

.card-meta {
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