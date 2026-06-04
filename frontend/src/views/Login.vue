<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api'

const router = useRouter()
const username = ref('')
const password = ref('')
const errorMsg = ref('')
const loading = ref(false)

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const result = await userApi.login(username.value, password.value)
    if (result.code === 0) {
      localStorage.setItem('token', result.data)
      router.push('/home/articles')
    } else {
      errorMsg.value = result.message
    }
  } catch (error) {
    errorMsg.value = '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-container">
    <!-- Subtle geometric background pattern -->
    <div class="background-pattern"></div>

    <div class="login-card">
      <!-- Brand / Logo Section -->
      <div class="brand-section">
        <div class="brand-logo">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48" fill="none" class="quill-icon">
            <path d="M36 4C36 4 28 8 22 16C16 24 14 32 12 38C10 44 8 46 8 46" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M36 4C36 4 38 10 36 18C34 26 28 34 24 38C20 42 16 44 14 44" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M8 46C8 46 10 44 14 44C18 44 20 46 20 46" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="36" cy="4" r="2" fill="currentColor"/>
          </svg>
        </div>
        <h1 class="brand-name">Inkwell</h1>
        <p class="brand-tagline">欢迎回来，请登录您的账户</p>
      </div>

      <!-- Decorative divider -->
      <div class="divider">
        <span class="divider-line"></span>
        <span class="divider-ornament"></span>
        <span class="divider-line"></span>
      </div>

      <!-- Login Form -->
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
            <input
              v-model="username"
              type="text"
              placeholder="请输入用户名"
              class="form-input"
              :class="{ 'is-error': errorMsg }"
            />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
            <input
              v-model="password"
              type="password"
              placeholder="请输入密码"
              class="form-input"
              :class="{ 'is-error': errorMsg }"
            />
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

        <button type="submit" class="login-btn" :disabled="loading">
          <svg v-if="loading" class="spinner" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none">
            <circle class="spinner-ring" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-dasharray="100"></circle>
          </svg>
          <span>{{ loading ? '登录中...' : '登录' }}</span>
        </button>
      </form>

      <!-- Register Link -->
      <div class="register-section">
        <span>还没有账号？</span>
        <button type="button" class="register-btn" @click="goRegister">立即注册</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ========================================
   Editorial Elegance — Login Page
   Warm, magazine-inspired design system
   ======================================== */

.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--bg-primary, #FAF7F2);
  position: relative;
  overflow: hidden;
  padding: 24px;
  font-family: var(--font-body, 'DM Sans', sans-serif);
}

/* --- Geometric background pattern (diagonal lines) --- */
.background-pattern {
  position: absolute;
  inset: 0;
  pointer-events: none;
  opacity: 0.035;
  background-image: repeating-linear-gradient(
    45deg,
    var(--text-primary, #2C2825) 0px,
    var(--text-primary, #2C2825) 1px,
    transparent 1px,
    transparent 28px
  );
  z-index: 0;
}

/* --- Login card --- */
.login-card {
  background: var(--bg-card, #FFFFFF);
  border: 1px solid var(--border-light, #E8E2D9);
  border-radius: var(--radius-xl, 20px);
  box-shadow: var(--shadow-lg, 0 20px 60px rgba(44, 40, 37, 0.08));
  padding: 52px 44px 44px;
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 10;
  animation: fadeInUp 0.7s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* --- Brand / Logo --- */
.brand-section {
  text-align: center;
  margin-bottom: 28px;
}

.brand-logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg, 14px);
  background: var(--accent-soft, #FDF0EC);
  color: var(--accent-color, #C45D3E);
  margin-bottom: 16px;
  transition: transform 0.3s ease;
}

.brand-logo:hover {
  transform: scale(1.05);
}

.quill-icon {
  width: 30px;
  height: 30px;
}

.brand-name {
  font-family: var(--font-display, 'DM Serif Display', serif);
  font-size: 2rem;
  font-weight: 400;
  color: var(--text-primary, #2C2825);
  margin: 0 0 6px;
  letter-spacing: -0.01em;
  line-height: 1.2;
}

.brand-tagline {
  font-size: 0.875rem;
  color: var(--text-muted, #9C9589);
  margin: 0;
  letter-spacing: 0.01em;
}

/* --- Decorative divider --- */
.divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: var(--border-light, #E8E2D9);
}

.divider-ornament {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent-muted, #D4A594);
  flex-shrink: 0;
}

/* --- Form --- */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.form-label {
  font-size: 0.8125rem;
  font-weight: 600;
  color: var(--text-secondary, #5C5650);
  letter-spacing: 0.02em;
  text-transform: uppercase;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--text-muted, #9C9589);
  transition: color 0.25s ease;
  pointer-events: none;
}

.form-input {
  width: 100%;
  padding: 13px 16px 13px 48px;
  border: 1.5px solid var(--border-color, #DDD6CC);
  border-radius: var(--radius-md, 10px);
  font-size: 0.9375rem;
  font-family: var(--font-body, 'DM Sans', sans-serif);
  color: var(--text-primary, #2C2825);
  background: var(--bg-secondary, #FDFBF8);
  transition: all 0.25s ease;
  outline: none;
  box-sizing: border-box;
}

.form-input::placeholder {
  color: var(--text-muted, #9C9589);
}

.form-input:hover {
  border-color: var(--accent-muted, #D4A594);
}

.form-input:focus {
  border-color: var(--accent-color, #C45D3E);
  background: var(--bg-card, #FFFFFF);
  box-shadow: 0 0 0 3px var(--accent-soft, #FDF0EC);
}

.form-input:focus ~ .input-icon,
.form-input:focus + .input-icon {
  color: var(--accent-color, #C45D3E);
}

.input-wrapper:focus-within .input-icon {
  color: var(--accent-color, #C45D3E);
}

.form-input.is-error {
  border-color: var(--error-color, #C53030);
}

.form-input.is-error:focus {
  box-shadow: 0 0 0 3px var(--error-soft, #FFF5F5);
}

/* --- Error message --- */
.error-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: var(--error-soft, #FFF5F5);
  border: 1px solid #FED7D7;
  border-radius: var(--radius-sm, 8px);
  color: var(--error-color, #C53030);
  font-size: 0.875rem;
  animation: fadeIn 0.25s ease;
}

.error-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

/* --- Login button --- */
.login-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 14px 24px;
  margin-top: 4px;
  background: var(--accent-color, #C45D3E);
  color: #FFFFFF;
  border: none;
  border-radius: var(--radius-md, 10px);
  font-size: 0.9375rem;
  font-weight: 600;
  font-family: var(--font-body, 'DM Sans', sans-serif);
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: var(--shadow-sm, 0 2px 8px rgba(196, 93, 62, 0.2));
  letter-spacing: 0.01em;
}

.login-btn:hover:not(:disabled) {
  background: var(--accent-hover, #B04E32);
  box-shadow: var(--shadow-md, 0 4px 16px rgba(196, 93, 62, 0.3));
  transform: translateY(-1px);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: var(--shadow-sm, 0 2px 8px rgba(196, 93, 62, 0.2));
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* --- Spinner --- */
.spinner {
  width: 18px;
  height: 18px;
}

.spinner-ring {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* --- Register section --- */
.register-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 28px;
  font-size: 0.875rem;
  color: var(--text-muted, #9C9589);
}

.register-btn {
  padding: 0;
  background: none;
  border: none;
  color: var(--accent-color, #C45D3E);
  font-weight: 600;
  font-family: var(--font-body, 'DM Sans', sans-serif);
  font-size: 0.875rem;
  cursor: pointer;
  text-decoration: none;
  border-bottom: 1.5px solid transparent;
  transition: all 0.2s ease;
  padding-bottom: 1px;
}

.register-btn:hover {
  color: var(--accent-hover, #B04E32);
  border-bottom-color: var(--accent-hover, #B04E32);
}

/* --- Animations --- */
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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* --- Responsive --- */
@media (max-width: 480px) {
  .login-card {
    padding: 40px 28px 36px;
    border-radius: var(--radius-lg, 14px);
  }

  .brand-name {
    font-size: 1.75rem;
  }
}
</style>
