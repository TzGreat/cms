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
    <div class="header">
      <h2>内容采集</h2>
      <button class="back-btn" @click="router.push('/home/articles')">← 返回列表</button>
    </div>

    <div class="input-section">
      <div class="input-group">
        <input
            v-model="url"
            type="text"
            placeholder="请输入文章URL（支持CSDN、掘金、知乎等）"
            :disabled="loading"
        />
        <button class="fetch-btn" @click="handleFetch" :disabled="loading">
          {{ loading ? '抓取中...' : '开始抓取' }}
        </button>
      </div>

      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
    </div>

    <div v-if="crawlResult" class="result-section">
      <div class="result-header">
        <h3>抓取结果预览</h3>
        <div class="source-info">
          来源：{{ crawlResult.sourceType }}
          <a :href="crawlResult.sourceUrl" target="_blank" class="source-link">查看原文</a>
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
          <span>内容长度：{{ crawlResult.content.length }} 字</span>
        </div>
      </div>

      <div class="action-buttons">
        <button class="reset-btn" @click="handleReset">重新抓取</button>
        <button class="save-btn" @click="handleSave" :disabled="loading">
          {{ loading ? '保存中...' : '保存为草稿' }}
        </button>
      </div>
    </div>

    <div class="tips">
      <h4>支持的站点：</h4>
      <ul>
        <li>CSDN 博客：https://blog.csdn.net/xxx/article/details/xxx</li>
        <li>掘金：https://juejin.cn/post/xxx</li>
        <li>知乎专栏：https://zhuanlan.zhihu.com/p/xxx</li>
        <li>SegmentFault：https://segmentfault.com/a/xxx</li>
        <li>其他支持标准HTML结构的文章页面</li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.crawl-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
  background: white;
  border-radius: 12px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header h2 {
  font-size: 20px;
}

.back-btn {
  padding: 8px 16px;
  background: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
}

.input-section {
  margin-bottom: 24px;
}

.input-group {
  display: flex;
  gap: 12px;
}

.input-group input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.fetch-btn {
  padding: 12px 24px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.fetch-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error-msg {
  color: #e74c3c;
  margin-top: 12px;
}

.result-section {
  margin-top: 32px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.source-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #666;
}

.source-link {
  color: #3498db;
  text-decoration: none;
}

.preview-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
}

.preview-title {
  font-size: 20px;
  margin-bottom: 16px;
  color: #333;
}

.preview-cover {
  margin-bottom: 16px;
  border-radius: 6px;
  overflow: hidden;
}

.preview-cover img {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
}

.preview-content {
  font-size: 14px;
  line-height: 1.8;
  color: #555;
  white-space: pre-wrap;
}

.preview-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
  font-size: 12px;
  color: #999;
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.reset-btn {
  padding: 10px 24px;
  background: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
}

.save-btn {
  padding: 10px 32px;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.save-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.tips {
  margin-top: 32px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.tips h4 {
  margin-bottom: 12px;
  color: #666;
}

.tips ul {
  list-style: none;
  padding: 0;
}

.tips li {
  padding: 6px 0;
  font-size: 13px;
  color: #888;
}
</style>