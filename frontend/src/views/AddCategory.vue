<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { categoryApi } from '@/api'

const router = useRouter()
const categoryName = ref('')
const categoryAlias = ref('')
const errorMsg = ref('')
const loading = ref(false)

const handleSubmit = async () => {
  if (!categoryName.value || !categoryAlias.value) {
    errorMsg.value = '请填写所有字段'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const result = await categoryApi.add({
      categoryName: categoryName.value,
      categoryAlias: categoryAlias.value
    })
    if (result.code === 0) {
      router.push('/home/categories')
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '添加失败'
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.push('/home/categories')
}
</script>

<template>
  <div class="form-container">
    <div class="form-card">
      <div class="card-header">
        <div class="header-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            <circle cx="12" cy="10" r="3"></circle>
          </svg>
        </div>
        <h2>添加分类</h2>
        <p>创建一个新的文章分类</p>
      </div>

      <form @submit.prevent="handleSubmit" class="category-form">
        <div v-if="errorMsg" class="error-message">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" x2="12" y1="8" y2="12"></line>
            <line x1="12" x2="12.01" y1="16" y2="16"></line>
          </svg>
          {{ errorMsg }}
        </div>

        <div class="form-group">
          <label class="form-label">分类名称</label>
          <input
            v-model="categoryName"
            type="text"
            placeholder="请输入分类名称"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label class="form-label">分类别名</label>
          <input
            v-model="categoryAlias"
            type="text"
            placeholder="请输入分类别名（英文）"
            class="form-input"
          />
        </div>

        <div class="btn-group">
          <button type="button" class="btn btn-secondary" @click="handleCancel">
            取消
          </button>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            <svg v-if="loading" class="spinner" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-dasharray="100" animation="spin 1s linear infinite"></circle>
            </svg>
            <span>{{ loading ? '提交中...' : '提交' }}</span>
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
  padding: 24px;
}

.form-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  padding: 36px 32px;
  width: 100%;
  max-width: 440px;
  border: 1px solid var(--border-light);
}

/* ── Header ── */
.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.header-icon {
  width: 56px;
  height: 56px;
  background: var(--accent-color);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 18px;
  color: #fff;
  box-shadow: var(--shadow-sm);
}

.header-icon svg {
  width: 26px;
  height: 26px;
}

.card-header h2 {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 400;
  color: var(--text-primary);
  margin: 0 0 6px;
  letter-spacing: -0.01em;
}

.card-header p {
  font-family: var(--font-body);
  font-size: 0.85rem;
  color: var(--text-muted);
  margin: 0;
  letter-spacing: 0.02em;
}

/* ── Form ── */
.category-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: var(--error-soft);
  border-radius: var(--radius-md);
  color: var(--error-color);
  font-family: var(--font-body);
  font-size: 0.85rem;
}

.error-message svg {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-family: var(--font-body);
  font-size: 0.7rem;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.form-input {
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-body);
  font-size: 0.9rem;
  color: var(--text-primary);
  transition: all 0.2s ease;
  background: var(--bg-secondary);
}

.form-input::placeholder {
  color: var(--text-muted);
}

.form-input:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px var(--accent-soft);
}

/* ── Buttons ── */
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
  font-family: var(--font-body);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
  border: none;
  letter-spacing: 0.01em;
}

.btn svg {
  width: 18px;
  height: 18px;
}

.btn-primary {
  background: var(--accent-color);
  color: #fff;
  box-shadow: var(--shadow-sm);
}

.btn-primary:hover:not(:disabled) {
  background: var(--accent-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-primary:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.btn-secondary {
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover {
  background: var(--bg-hover);
  border-color: var(--border-color);
}

.spinner {
  width: 18px;
  height: 18px;
}

.spinner circle {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
