<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { userApi, type User } from '@/api'

const user = ref<User | null>(null)
const editing = ref(false)
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const errorMsg = ref('')
const successMsg = ref('')
const passwordError = ref('')

const fetchUserInfo = async () => {
  try {
    const result = await userApi.getUserInfo()
    if (result.code === 0) {
      user.value = result.data
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

const handleUpdate = async () => {
  if (!user.value) return
  try {
    const result = await userApi.updateUser({
      nickname: user.value.nickname,
      email: user.value.email,
      userPic: user.value.userPic
    })
    if (result.code === 0) {
      successMsg.value = '更新成功'
      editing.value = false
      setTimeout(() => successMsg.value = '', 3000)
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '更新失败'
  }
}

const validatePassword = () => {
  if (!oldPassword.value) {
    passwordError.value = '请输入原密码'
    return false
  }
  if (!newPassword.value) {
    passwordError.value = '请输入新密码'
    return false
  }
  if (newPassword.value.length < 5 || newPassword.value.length > 16) {
    passwordError.value = '密码长度必须在5-16位之间'
    return false
  }
  if (newPassword.value !== confirmPassword.value) {
    passwordError.value = '两次密码不一致'
    return false
  }
  passwordError.value = ''
  return true
}

const handleChangePassword = async () => {
  if (!validatePassword()) return

  try {
    const result = await userApi.updatePassword(oldPassword.value, newPassword.value, confirmPassword.value)
    if (result.code === 0) {
      successMsg.value = '密码修改成功'
      oldPassword.value = ''
      newPassword.value = ''
      confirmPassword.value = ''
      setTimeout(() => successMsg.value = '', 3000)
    } else {
      passwordError.value = result.message
    }
  } catch (error) {
    passwordError.value = '修改失败'
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <div class="profile">
    <div class="profile-header">
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <img v-if="user?.userPic" :src="user.userPic" alt="头像" class="avatar" />
          <div v-else class="avatar-placeholder">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </div>
        </div>
        <h2 class="username">{{ user?.username }}</h2>
        <p class="user-role">管理员</p>
      </div>
    </div>

    <div class="profile-content">
      <div class="section">
        <div class="section-header">
          <h3 class="section-title">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
            <span>个人信息</span>
          </h3>
        </div>

        <div v-if="successMsg" class="alert alert-success">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="20 6 9 17 4 12"></polyline>
          </svg>
          {{ successMsg }}
        </div>
        <div v-if="errorMsg" class="alert alert-error">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" x2="9" y1="9" y2="15"></line>
            <line x1="9" x2="15" y1="9" y2="15"></line>
          </svg>
          {{ errorMsg }}
        </div>

        <form v-if="user" class="form" @submit.prevent="handleUpdate">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">用户名</label>
              <input :value="user.username" disabled type="text" class="form-input" />
            </div>
            <div class="form-group">
              <label class="form-label">昵称</label>
              <input v-model="user.nickname" :disabled="!editing" type="text" class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">邮箱</label>
              <input v-model="user.email" :disabled="!editing" type="email" class="form-input" />
            </div>
            <div class="form-group">
              <label class="form-label">头像URL</label>
              <input v-model="user.userPic" :disabled="!editing" type="text" class="form-input" placeholder="输入头像链接" />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">创建时间</label>
            <input :value="user.createTime" disabled type="text" class="form-input" />
          </div>

          <div class="btn-group">
            <button v-if="!editing" type="button" class="btn btn-primary" @click="editing = true">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
              </svg>
              <span>编辑信息</span>
            </button>
            <template v-else>
              <button type="submit" class="btn btn-success">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
                <span>保存</span>
              </button>
              <button type="button" class="btn btn-secondary" @click="editing = false; fetchUserInfo()">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="18" x2="6" y1="6" y2="18"></line>
                  <line x1="6" x2="18" y1="6" y2="18"></line>
                </svg>
                <span>取消</span>
              </button>
            </template>
          </div>
        </form>
      </div>

      <div class="section">
        <div class="section-header">
          <h3 class="section-title">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
            <span>修改密码</span>
          </h3>
        </div>

        <div v-if="passwordError" class="alert alert-error">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" x2="9" y1="9" y2="15"></line>
            <line x1="9" x2="15" y1="9" y2="15"></line>
          </svg>
          {{ passwordError }}
        </div>

        <form class="form" @submit.prevent="handleChangePassword">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">原密码</label>
              <div class="input-wrapper">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                <input v-model="oldPassword" type="password" class="form-input" placeholder="请输入原密码" />
              </div>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">新密码</label>
              <div class="input-wrapper">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                <input v-model="newPassword" type="password" class="form-input" placeholder="请输入新密码" />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">确认密码</label>
              <div class="input-wrapper">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                <input v-model="confirmPassword" type="password" class="form-input" placeholder="请再次输入新密码" />
              </div>
            </div>
          </div>

          <div class="form-hint">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" x2="12.01" y1="8" y2="12"></line>
              <line x1="12" x2="12.01" y1="16" y2="16"></line>
            </svg>
            <span>密码长度需在5-16位之间</span>
          </div>

          <button type="submit" class="btn btn-primary btn-full-width">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 21l-6-6m2-5a7 7 0 1 1-14 0 7 7 0 0 1 14 0z"></path>
            </svg>
            <span>修改密码</span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile {
  max-width: 700px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  padding: 32px;
  margin-bottom: 24px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-wrapper {
  position: relative;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--accent-color);
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.3);
}

.avatar-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent-color) 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  border: 4px solid var(--accent-color);
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.3);
}

.avatar-placeholder svg {
  width: 50px;
  height: 50px;
}

.username {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.user-role {
  font-size: 0.875rem;
  color: var(--text-muted);
  margin: 0;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  padding: 24px;
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.section-title svg {
  width: 20px;
  height: 20px;
  color: var(--accent-color);
}

.alert {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-radius: var(--radius-md);
  margin-bottom: 16px;
  font-size: 0.875rem;
}

.alert svg {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.alert-success {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.alert-error {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-secondary);
}

.form-input {
  padding: 12px 14px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  transition: all 0.2s ease;
}

.form-input:focus {
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input:disabled {
  background: var(--bg-secondary);
  color: var(--text-muted);
  cursor: not-allowed;
}

.input-wrapper {
  position: relative;
}

.input-wrapper svg {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--text-muted);
}

.input-wrapper .form-input {
  padding-left: 48px;
}

.form-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.75rem;
  color: var(--text-muted);
  padding: 10px 14px;
  background: var(--bg-secondary);
  border-radius: var(--radius-sm);
}

.form-hint svg {
  width: 14px;
  height: 14px;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
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

.btn-full-width {
  width: 100%;
  justify-content: center;
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

.btn-success {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.3);
}

.btn-success:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(34, 197, 94, 0.4);
}

.btn-secondary {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover {
  background: var(--bg-hover);
}
</style>