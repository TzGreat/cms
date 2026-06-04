<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { crawlerApi } from '@/api'

const router = useRouter()
const url = ref('')
const loading = ref(false)
const errorMsg = ref('')
const crawlResult = ref<{
  title: string
  content: string
  coverImg: string
  sourceUrl: string
  sourceType: string
} | null>(null)

const handleFetch = async () => {
  if (!url.value.trim()) {
    errorMsg.value = '请输入URL'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const result = await crawlerApi.fetch(url.value)
    if (result.code === 0) {
      crawlResult.value = result.data
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '抓取失败，请检查URL是否正确'
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (!crawlResult.value) return

  loading.value = true
  errorMsg.value = ''

  try {
    const result = await crawlerApi.save(url.value, 1)
    if (result.code === 0) {
      alert('文章保存成功，已作为草稿保存')
      crawlResult.value = null
      url.value = ''
      router.push('/home/articles')
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '保存失败'
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  url.value = ''
  crawlResult.value = null
  errorMsg.value = ''
}
</script>

<template>
  <div class="crawl-container">
    <header class="header">
      <h2 class="page-title">内容采集</h2>
      <button class="back-btn" @click="router.push('/home/articles')">
        <span class="back-icon">&larr;</span>
        <span>返回列表</span>
      </button>
    </header>

    <section class="input-section">
      <div class="input-group">
        <input
          v-model="url"
          type="text"
          class="url-input"
          placeholder="请输入文章URL（支持CSDN、掘金、知乎等）"
          :disabled="loading"
        />
        <button class="fetch-btn" @click="handleFetch" :disabled="loading">
          <span v-if="loading" class="btn-loading">抓取中...</span>
          <span v-else>开始抓取</span>
        </button>
      </div>

      <div v-if="errorMsg" class="error-msg">
        <span class="error-icon">!</span>
        <span>{{ errorMsg }}</span>
      </div>
    </section>

    <section v-if="crawlResult" class="result-section">
      <div class="result-header">
        <h3 class="result-title">抓取结果预览</h3>
        <div class="source-info">
          <span class="source-badge">{{ crawlResult.sourceType }}</span>
          <a :href="crawlResult.sourceUrl" target="_blank" class="source-link">查看原文 &rarr;</a>
        </div>
      </div>

      <div class="preview-card">
        <h1 class="preview-title">{{ crawlResult.title }}</h1>

        <div v-if="crawlResult.coverImg" class="preview-cover">
          <img :src="crawlResult.coverImg" :alt="crawlResult.title" />
        </div>

        <div class="preview-content">
          {{ crawlResult.content.substring(0, 500) }}...
        </div>

        <div class="preview-footer">
          <span class="content-length">内容长度：{{ crawlResult.content.length }} 字</span>
        </div>
      </div>

      <div class="action-buttons">
        <button class="reset-btn" @click="handleReset">重新抓取</button>
        <button class="save-btn" @click="handleSave" :disabled="loading">
          <span v-if="loading">保存中...</span>
          <span v-else>保存为草稿</span>
        </button>
      </div>
    </section>

    <section class="tips">
      <h4 class="tips-title">支持的站点</h4>
      <ul class="tips-list">
        <li>CSDN 博客：https://blog.csdn.net/xxx/article/details/xxx</li>
        <li>掘金：https://juejin.cn/post/xxx</li>
        <li>知乎专栏：https://zhuanlan.zhihu.com/p/xxx</li>
        <li>SegmentFault：https://segmentfault.com/a/xxx</li>
        <li>其他支持标准HTML结构的文章页面</li>
      </ul>
    </section>
  </div>
</template>

<style scoped>
.crawl-container {
  --bg-primary: #FEFCF9;
  --bg-secondary: #F7F3EE;
  --bg-tertiary: #EDE8E1;
  --bg-card: #FFFFFF;
  --bg-hover: #F9F5F0;
  --bg-inset: #FAF8F5;
  --text-primary: #2C2420;
  --text-secondary: #6B5E54;
  --text-muted: #A89B91;
  --accent-color: #C45D3E;
  --accent-hover: #B04E32;
  --accent-soft: #FDF0EC;
  --accent-muted: #E8A898;
  --success-color: #3A8A5C;
  --success-soft: #EEFAF3;
  --warning-color: #C49A3E;
  --warning-soft: #FDF8EC;
  --error-color: #C43E3E;
  --error-soft: #FDECEC;
  --border-color: #E5DDD5;
  --border-light: #F0EBE5;
  --shadow-sm: 0 1px 3px rgba(44, 36, 32, 0.06);
  --shadow-md: 0 4px 12px rgba(44, 36, 32, 0.08);
  --shadow-lg: 0 8px 30px rgba(44, 36, 32, 0.1);
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 14px;
  --radius-xl: 20px;
  --font-display: 'DM Serif Display', Georgia, 'Times New Roman', serif;
  --font-body: 'DM Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  max-width: 800px;
  margin: 0 auto;
  padding: 36px 40px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  font-family: var(--font-body);
  color: var(--text-primary);
}

/* ── Header ── */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-light);
}

.page-title {
  font-family: var(--font-display);
  font-size: 26px;
  font-weight: 400;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: var(--bg-hover);
  border-color: var(--text-muted);
  color: var(--text-primary);
}

.back-icon {
  font-size: 15px;
  line-height: 1;
}

/* ── Input Section ── */
.input-section {
  margin-bottom: 28px;
}

.input-group {
  display: flex;
  gap: 12px;
}

.url-input {
  flex: 1;
  padding: 12px 18px;
  background: var(--bg-inset);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--text-primary);
  transition: all 0.2s ease;
  outline: none;
}

.url-input::placeholder {
  color: var(--text-muted);
}

.url-input:focus {
  border-color: var(--accent-muted);
  background: var(--bg-card);
  box-shadow: 0 0 0 3px var(--accent-soft);
}

.url-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.fetch-btn {
  padding: 12px 28px;
  background: var(--accent-color);
  color: var(--bg-card);
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.01em;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.fetch-btn:hover:not(:disabled) {
  background: var(--accent-hover);
  box-shadow: var(--shadow-sm);
}

.fetch-btn:active:not(:disabled) {
  transform: translateY(1px);
}

.fetch-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

/* ── Error Message ── */
.error-msg {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 14px;
  padding: 12px 16px;
  background: var(--error-soft);
  border: 1px solid var(--error-color);
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--error-color);
}

.error-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background: var(--error-color);
  color: var(--bg-card);
  border-radius: 50%;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

/* ── Result Section ── */
.result-section {
  margin-top: 36px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.result-title {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 400;
  color: var(--text-primary);
}

.source-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: var(--text-secondary);
}

.source-badge {
  padding: 4px 10px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  font-size: 12px;
  color: var(--text-secondary);
}

.source-link {
  color: var(--accent-color);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;
}

.source-link:hover {
  color: var(--accent-hover);
  text-decoration: underline;
}

/* ── Preview Card ── */
.preview-card {
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 24px;
  background: var(--bg-inset);
  box-shadow: var(--shadow-sm);
}

.preview-title {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 400;
  margin-bottom: 18px;
  color: var(--text-primary);
  line-height: 1.35;
}

.preview-cover {
  margin-bottom: 18px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  border: 1px solid var(--border-light);
}

.preview-cover img {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  display: block;
}

.preview-content {
  font-size: 14px;
  line-height: 1.85;
  color: var(--text-secondary);
  white-space: pre-wrap;
}

.preview-footer {
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid var(--border-light);
}

.content-length {
  font-size: 12px;
  color: var(--text-muted);
  letter-spacing: 0.02em;
}

/* ── Action Buttons ── */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: 22px;
}

.reset-btn {
  padding: 10px 26px;
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  cursor: pointer;
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.reset-btn:hover {
  background: var(--bg-hover);
  border-color: var(--text-muted);
  color: var(--text-primary);
}

.save-btn {
  padding: 10px 32px;
  background: var(--success-color);
  color: var(--bg-card);
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.save-btn:hover:not(:disabled) {
  opacity: 0.9;
  box-shadow: var(--shadow-sm);
}

.save-btn:active:not(:disabled) {
  transform: translateY(1px);
}

.save-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

/* ── Tips Section ── */
.tips {
  margin-top: 36px;
  padding: 24px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
}

.tips-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 400;
  margin-bottom: 14px;
  color: var(--text-secondary);
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  padding: 7px 0;
  font-size: 13px;
  color: var(--text-muted);
  border-bottom: 1px solid var(--border-light);
  line-height: 1.6;
}

.tips-list li:last-child {
  border-bottom: none;
  padding-bottom: 0;
}
</style>
