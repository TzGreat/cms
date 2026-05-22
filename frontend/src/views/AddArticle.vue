<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { articleApi, categoryApi, ocrApi, type Category } from '@/api'

const router = useRouter()
const title = ref('')
const content = ref('')
const coverImg = ref('')
const state = ref('draft')
const categoryId = ref<number | undefined>()
const categories = ref<Category[]>([])
const errorMsg = ref('')
const ocrLoading = ref(false)
const ocrResult = ref('')

// OCR文件输入引用
const ocrFileRef = ref<HTMLInputElement | null>(null)

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

/**
 * 图片文字识别
 */
const handleOcr = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    errorMsg.value = '请选择图片文件'
    return
  }

  ocrLoading.value = true
  errorMsg.value = ''

  try {
    const result = await ocrApi.recognize(file)
    if (result.code === 0) {
      ocrResult.value = result.data
      // 将识别结果追加到内容中
      content.value += '\n' + result.data
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '识别失败，请重试'
    console.error('OCR识别失败', error)
  } finally {
    ocrLoading.value = false
    // 重置文件输入
    target.value = ''
  }
}

const handleSubmit = async () => {
  if (!title.value || !content.value || !coverImg.value || !categoryId.value) {
    errorMsg.value = '请填写所有必填字段'
    return
  }

  try {
    const result = await articleApi.add({
      title: title.value,
      content: content.value,
      coverImg: coverImg.value,
      state: state.value,
      categoryId: categoryId.value
    })
    if (result.code === 0) {
      router.push('/home/articles')
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '添加失败'
  }
}

const handleCancel = () => {
  router.push('/home/articles')
}

onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="add-article">
    <h2>添加文章</h2>
    <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
    <div class="form">
      <div class="form-group">
        <label>标题</label>
        <input v-model="title" type="text" placeholder="请输入文章标题（最多10个字符）" />
      </div>
      <div class="form-group">
        <label>内容</label>
        <textarea v-model="content" rows="6" placeholder="请输入文章内容"></textarea>
        <!-- OCR识别按钮 -->
        <div class="ocr-section">
          <input 
            ref="ocrFileRef"
            type="file" 
            accept="image/*" 
            class="ocr-file-input"
            @change="handleOcr"
          />
          <button 
            class="ocr-btn" 
            :disabled="ocrLoading"
            @click="ocrFileRef?.click()"
          >
            <span v-if="ocrLoading">识别中...</span>
            <span v-else>📷 图片识别文字</span>
          </button>
          <div v-if="ocrResult" class="ocr-result">
            <strong>识别结果：</strong>{{ ocrResult }}
          </div>
        </div>
      </div>
      <div class="form-group">
        <label>封面图片URL</label>
        <input v-model="coverImg" type="text" placeholder="请输入封面图片URL" />
      </div>
      <div class="form-group">
        <label>分类</label>
        <select v-model="categoryId">
          <option :value="undefined">请选择分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.categoryName }}
          </option>
        </select>
      </div>
      <div class="form-group">
        <label>状态</label>
        <select v-model="state">
          <option value="d">草稿</option>
          <option value="p">已发布</option>
        </select>
      </div>
      <div class="btn-group">
        <button class="submit-btn" @click="handleSubmit">提交</button>
        <button class="cancel-btn" @click="handleCancel">取消</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.add-article {
  max-width: 600px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  padding: 24px;
}

.add-article h2 {
  font-size: 20px;
  margin-bottom: 20px;
}

.error-msg {
  color: #e74c3c;
  margin-bottom: 15px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
}

.ocr-section {
  margin-top: 10px;
}

.ocr-file-input {
  display: none;
}

.ocr-btn {
  padding: 8px 16px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.ocr-btn:disabled {
  background: #95a5a6;
  cursor: not-allowed;
}

.ocr-result {
  margin-top: 10px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 14px;
  color: #333;
  max-height: 150px;
  overflow-y: auto;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.submit-btn {
  padding: 10px 32px;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.cancel-btn {
  padding: 10px 32px;
  background: #95a5a6;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>