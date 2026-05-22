<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { categoryApi, type Category } from '@/api'

const router = useRouter()
const route = useRoute()
const category = ref<Category | null>(null)
const errorMsg = ref('')
const loading = ref(true)
const submitting = ref(false)

const fetchCategory = async () => {
  const id = parseInt(route.params.id as string)
  if (!id) return

  try {
    const result = await categoryApi.getById(id)
    if (result.code === 0) {
      category.value = result.data
    }
  } catch (error) {
    console.error('获取分类详情失败', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!category.value) return
  if (!category.value.categoryName || !category.value.categoryAlias) {
    errorMsg.value = '请填写所有字段'
    return
  }

  submitting.value = true
  errorMsg.value = ''

  try {
    const result = await categoryApi.update(category.value)
    if (result.code === 0) {
      router.push('/home/categories')
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '更新失败'
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  router.push('/home/categories')
}

onMounted(() => {
  fetchCategory()
})
</script>

<template>
  <div class="form-container">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <span class="loading-text">加载中...</span>
    </div>

    <div v-else class="form-card">
      <div class="card-header">
        <div class="header-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
          </svg>
        </div>
        <h2>编辑分类</h2>
        <p>修改分类信息</p>
      </div>

      <form @submit.prevent="handleSubmit" class="category-form">
        <div v-if="errorMsg" class="error-message">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" x2="12" y1="8" y2="12"></line>
            <line x1="12" x2="12.01" y1="16" y2="16"></line>
          </svg>
          {{ errorMsg }}
        </div>

        <div class="form-group">
          <label class="form-label">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            分类名称
          </label>
          <input 
            v-model="category?.categoryName" 
            type="text" 
            placeholder="请输入分类名称" 
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label class="form-label">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
            分类别名
          </label>
          <input 
            v-model="category?.categoryAlias" 
            type="text" 
            placeholder="请输入分类别名（英文）" 
            class="form-input"
          />
        </div>

        <div class="btn-group">
          <button type="button" class="btn btn-secondary" @click="handleCancel">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="9" x2="15" y1="9" y2="15"></line>
              <line x1="15" x2="9" y1="9" y2="15"></line>
            </svg>
            取消
          </button>
          <button type="submit" class="btn btn-primary" :disabled="submitting">
            <svg v-if="submitting" class="spinner" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-dasharray="100" animation="spin 1s linear infinite"></circle>
            </svg>
            <span>{{ submitting ? '保存中...' : '保存' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  padding: 20px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
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
}

.form-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  padding: 32px;
  width: 100%;
  max-width: 450px;
}

.card-header {
  text-align: center;
  margin-bottom: 28px;
}

.header-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: white;
}

.header-icon svg {
  width: 28px;
  height: 28px;
}

.card-header h2 {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.card-header p {
  font-size: 0.875rem;
  color: var(--text-muted);
  margin: 0;
}

.category-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fef2f2;
  border-radius: var(--radius-md);
  color: #dc2626;
  font-size: 0.875rem;
}

.error-message svg {
  width: 16px;
  height: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-secondary);
}

.form-label svg {
  width: 16px;
  height: 16px;
}

.form-input {
  padding: 14px 16px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: 0.9rem;
  transition: all 0.2s ease;
  background: var(--bg-secondary);
}

.form-input:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.1);
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.btn {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: var(--radius-md);
  font-size: 0.9rem;
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
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(245, 158, 11, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.4);
}

.btn-secondary {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 2px solid var(--border-color);
}

.btn-secondary:hover {
  background: var(--border-light);
}

.spinner {
  width: 18px;
  height: 18px;
}

.spinner circle {
  animation: spin 1s linear infinite;
}
</style>