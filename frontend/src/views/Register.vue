<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api'

const router = useRouter()
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMsg = ref('')
const loading = ref(false)

const handleRegister = async () => {
  if (!username.value || !password.value || !confirmPassword.value) {
    errorMsg.value = '请填写所有字段'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  if (username.value.length < 5 || username.value.length > 16) {
    errorMsg.value = '用户名长度必须在5-16位之间'
    return
  }

  if (password.value.length < 5 || password.value.length > 16) {
    errorMsg.value = '密码长度必须在5-16位之间'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const result = await userApi.register(username.value, password.value)
    if (result.code === 0) {
      router.push('/login')
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const goLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-container">
    <div class="background-pattern"></div>

    <div class="register-card">
      <div class="logo-section">
        <div class="logo-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 3a2.83 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"></path>
            <path d="m15 5 4 4"></path>
          </svg>
        </div>
        <h1 class="brand-title">创建账户</h1>
        <p class="brand-subtitle">加入我们，开始您的内容管理之旅</p>
      </div>

      <div class="divider">
        <span class="divider-line"></span>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
            <input
              v-model="username"
              type="text"
              placeholder="请输入用户名（5-16位）"
              class="form-input"
            />
            <span v-if="username.length >= 5 && username.length <= 16" class="validation-icon valid">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </span>
          </div>
          <span class="input-hint">用户名长度需在5-16位之间</span>
        </div>

        <div class="form-group">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
            <input
              v-model="password"
              type="password"
              placeholder="请输入密码（5-16位）"
              class="form-input"
            />
          </div>
          <span class="input-hint">密码长度需在5-16位之间</span>
        </div>

        <div class="form-group">
          <label class="form-label">确认密码</label>
          <div class="input-wrapper">
            <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 12l2 2 4-4m6 2a9 9 0 1 1-18 0 9 9 0 0 1 18 0z"></path>
            </svg>
            <input
              v-model="confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              class="form-input"
              :class="{ 'is-error': confirmPassword && password !== confirmPassword }"
            />
            <span v-if="confirmPassword && password === confirmPassword" class="validation-icon valid">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </span>
            <span v-else-if="confirmPassword && password !== confirmPassword" class="validation-icon invalid">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" x2="6" y1="6" y2="18"></line>
                <line x1="6" x2="18" y1="6" y2="18"></line>
              </svg>
            </span>
          </div>
        </div>

        <div v-if="errorMsg" class="error-message">
          <svg xmlns="http://www.w3.org/2000/svg" class="error-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" x2="12" y1="8" y2="12"></line>
            <line x1="12" x2="12.01" y1="16" y2="16"></line>
          </svg>
          {{ errorMsg }}
        </div>

        <button type="submit" class="register-btn" :disabled="loading">
          <svg v-if="loading" class="spinner" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none">
            <circle class="spinner-ring" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-dasharray="100"></circle>
          </svg>
          <span>{{ loading ? '注册中...' : '立即注册' }}</span>
        </button>
      </form>

      <div class="login-section">
        <span class="login-hint">已有账号？</span>
        <button type="button" class="login-link" @click="goLogin">立即登录</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--bg-primary, #FAF7F2);
  position: relative;
  overflow: hidden;
  padding: 20px;
  font-family: var(--font-body, 'DM Sans', -apple-system, BlinkMacSystemFont, sans-serif);
}

/* Subtle diagonal line pattern background */
.background-pattern {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background-image: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 39px,
    rgba(196, 93, 62, 0.04) 39px,
    rgba(196, 93, 62, 0.04) 40px
  );
  z-index: 0;
}

.register-card {
  background: var(--bg-card, #FFFFFF);
  border: 1px solid var(--border-light, #E8E0D6);
  border-radius: var(--radius-xl, 20px);
  box-shadow: var(--shadow-lg, 0 20px 60px rgba(0, 0, 0, 0.08));
  padding: 48px 40px;
  width: 100%;
  max-width: 440px;
  position: relative;
  z-index: 10;
  animation: fadeInUp 0.6s cubic-bezier(0.22, 1, 0.36, 1) forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Logo Section */
.logo-section {
  text-align: center;
  margin-bottom: 28px;
}

.logo-icon {
  width: 56px;
  height: 56px;
  background-color: var(--accent-color, #C45D3E);
  border-radius: var(--radius-lg, 16px);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 18px;
  color: #FFFFFF;
  box-shadow: 0 4px 16px rgba(196, 93, 62, 0.25);
}

.logo-icon svg {
  width: 26px;
  height: 26px;
}

.brand-title {
  font-family: var(--font-display, 'DM Serif Display', Georgia, serif);
  font-size: 1.65rem;
  font-weight: 400;
  color: var(--text-primary, #2C2417);
  margin-bottom: 6px;
  letter-spacing: -0.01em;
}

.brand-subtitle {
  font-size: 0.875rem;
  color: var(--text-muted, #9C8E7E);
  line-height: 1.5;
}

/* Divider */
.divider {
  display: flex;
  align-items: center;
  margin-bottom: 28px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: var(--border-light, #E8E0D6);
}

/* Form */
.register-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 0.8125rem;
  font-weight: 600;
  color: var(--text-secondary, #5C4F3D);
  letter-spacing: 0.02em;
  text-transform: uppercase;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--text-muted, #9C8E7E);
  transition: color 0.2s ease;
  pointer-events: none;
}

.form-input {
  width: 100%;
  padding: 13px 44px 13px 44px;
  border: 1.5px solid var(--border-color, #DDD4C8);
  border-radius: var(--radius-md, 10px);
  font-size: 0.9rem;
  font-family: var(--font-body, 'DM Sans', sans-serif);
  color: var(--text-primary, #2C2417);
  background: var(--bg-secondary, #FDFBF8);
  transition: all 0.2s ease;
  outline: none;
  box-sizing: border-box;
}

.form-input::placeholder {
  color: var(--text-muted, #9C8E7E);
}

.form-input:focus {
  border-color: var(--accent-color, #C45D3E);
  background: var(--bg-card, #FFFFFF);
  box-shadow: 0 0 0 3px var(--accent-soft, rgba(196, 93, 62, 0.1));
}

.form-input:focus ~ .input-icon,
.form-input:focus + .input-icon {
  color: var(--accent-color, #C45D3E);
}

.input-wrapper:focus-within .input-icon {
  color: var(--accent-color, #C45D3E);
}

.form-input.is-error {
  border-color: var(--error-color, #D94040);
  background: var(--error-soft, #FEF2F2);
}

.form-input.is-error:focus {
  box-shadow: 0 0 0 3px rgba(217, 64, 64, 0.1);
}

/* Validation Icons */
.validation-icon {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.validation-icon svg {
  width: 16px;
  height: 16px;
}

.validation-icon.valid {
  color: var(--success-color, #3D8B5E);
}

.validation-icon.invalid {
  color: var(--error-color, #D94040);
}

.input-hint {
  font-size: 0.75rem;
  color: var(--text-muted, #9C8E7E);
  padding-left: 2px;
}

/* Error Message */
.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  background: var(--error-soft, #FEF2F2);
  border: 1px solid rgba(217, 64, 64, 0.12);
  border-radius: var(--radius-sm, 8px);
  color: var(--error-color, #D94040);
  font-size: 0.8125rem;
  animation: fadeIn 0.25s ease;
}

.error-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Register Button */
.register-btn {
  width: 100%;
  padding: 14px 24px;
  background-color: var(--accent-color, #C45D3E);
  color: #FFFFFF;
  border: none;
  border-radius: var(--radius-md, 10px);
  font-size: 0.9375rem;
  font-weight: 600;
  font-family: var(--font-body, 'DM Sans', sans-serif);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(196, 93, 62, 0.2);
  margin-top: 4px;
}

.register-btn:hover:not(:disabled) {
  background-color: var(--accent-hover, #B5502F);
  box-shadow: 0 4px 14px rgba(196, 93, 62, 0.3);
  transform: translateY(-1px);
}

.register-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(196, 93, 62, 0.2);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Spinner */
.spinner {
  width: 18px;
  height: 18px;
}

.spinner-ring {
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Login Section */
.login-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid var(--border-light, #E8E0D6);
}

.login-hint {
  font-size: 0.8125rem;
  color: var(--text-muted, #9C8E7E);
}

.login-link {
  padding: 0;
  background: none;
  border: none;
  color: var(--accent-color, #C45D3E);
  font-size: 0.8125rem;
  font-weight: 600;
  font-family: var(--font-body, 'DM Sans', sans-serif);
  cursor: pointer;
  text-decoration: none;
  position: relative;
  transition: color 0.2s ease;
}

.login-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1.5px;
  background-color: var(--accent-color, #C45D3E);
  transition: width 0.25s ease;
}

.login-link:hover {
  color: var(--accent-hover, #B5502F);
}

.login-link:hover::after {
  width: 100%;
}

/* Responsive */
@media (max-width: 480px) {
  .register-card {
    padding: 36px 24px;
    border-radius: var(--radius-lg, 16px);
  }

  .brand-title {
    font-size: 1.4rem;
  }
}
</style>
