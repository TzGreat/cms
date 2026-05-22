<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { userApi, type User } from '@/api'
import { theme, toggleTheme } from '../utils/theme'

const router = useRouter()
const route = useRoute()
const user = ref<User | null>(null)
const sidebarCollapsed = ref(false)

onMounted(async () => {
  try {
    const result = await userApi.getUserInfo()
    if (result.code === 0) {
      user.value = result.data
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
})

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}

const navItems = [
  { name: '文章列表', path: '/home/articles', icon: 'article' },
  { name: '分类管理', path: '/home/categories', icon: 'category' },
  { name: '个人中心', path: '/home/profile', icon: 'profile' }
]
</script>

<template>
  <div class="home-container">
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="logo-section">
        <div class="logo">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M15 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7Z"></path>
            <polyline points="15 2 15 8 21 8"></polyline>
            <line x1="16" x2="8" y1="13" y2="13"></line>
            <line x1="16" x2="8" y1="17" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
        </div>
        <span v-if="!sidebarCollapsed" class="logo-text">管理系统</span>
      </div>

      <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline v-if="!sidebarCollapsed" points="15 18 9 12 15 6"></polyline>
          <polyline v-else points="9 18 15 12 9 6"></polyline>
        </svg>
      </button>

      <nav class="nav">
        <a
          v-for="item in navItems"
          :key="item.path"
          :href="item.path"
          :class="['nav-item', { active: route.path === item.path }]"
        >
          <svg v-if="item.icon === 'article'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" x2="8" y1="13" y2="13"></line>
            <line x1="16" x2="8" y1="17" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
          <svg v-else-if="item.icon === 'category'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            <circle cx="12" cy="10" r="3"></circle>
          </svg>
          <svg v-else-if="item.icon === 'profile'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          <span v-if="!sidebarCollapsed" class="nav-text">{{ item.name }}</span>
        </a>
      </nav>
    </aside>

    <main class="main-content">
      <header class="header">
        <div class="header-left">
          <h1 class="page-title">
            {{ navItems.find(item => item.path === route.path)?.name || '管理系统' }}
          </h1>
        </div>
        <div class="header-right">
          <button 
            class="theme-toggle" 
            @click="toggleTheme" 
            :title="theme === 'light' ? '深色模式' : '浅色模式'"
          >
            <svg v-if="theme === 'light'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="5"></circle>
              <line x1="12" x2="12" y1="1" y2="3"></line>
              <line x1="12" x2="12" y1="21" y2="23"></line>
              <line x1="4.22" x2="5.64" y1="4.22" y2="5.64"></line>
              <line x1="18.36" x2="19.78" y1="18.36" y2="19.78"></line>
              <line x1="1" x2="3" y1="12" y2="12"></line>
              <line x1="21" x2="23" y1="12" y2="12"></line>
              <line x1="4.22" x2="5.64" y1="19.78" y2="18.36"></line>
              <line x1="18.36" x2="19.78" y1="5.64" y2="4.22"></line>
            </svg>
          </button>

          <div class="user-menu">
            <div class="user-avatar-wrapper">
              <img v-if="user?.userPic" :src="user.userPic" alt="头像" class="user-avatar" />
              <div v-else class="avatar-placeholder">{{ user?.username?.charAt(0) }}</div>
            </div>
            <div class="user-info" v-if="!sidebarCollapsed">
              <span class="user-name">{{ user?.nickname || user?.username }}</span>
              <span class="user-role">管理员</span>
            </div>
            <div class="user-dropdown">
              <button class="dropdown-btn" @click="handleLogout">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                  <polyline points="16 17 21 12 16 7"></polyline>
                  <line x1="21" x2="9" y1="12" y2="12"></line>
                </svg>
                <span>退出登录</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      <div class="content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  min-height: 100vh;
  background: var(--bg-secondary);
}

.sidebar {
  width: 250px;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  color: #e2e8f0;
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  transition: width 0.3s ease;
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed {
  width: 72px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 20px;
}

.logo {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: white;
}

.logo-text {
  font-size: 1.1rem;
  font-weight: 700;
  color: white;
}

.collapse-btn {
  position: absolute;
  right: -12px;
  top: 24px;
  width: 24px;
  height: 24px;
  background: var(--bg-card);
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: all 0.2s ease;
}

.collapse-btn:hover {
  background: var(--accent-color);
  color: white;
}

.nav {
  flex: 1;
  padding: 0 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin-bottom: 6px;
  border-radius: 10px;
  color: #94a3b8;
  text-decoration: none;
  transition: all 0.25s ease;
  position: relative;
}

.nav-item svg {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.nav-item:hover {
  background: rgba(59, 130, 246, 0.1);
  color: #e2e8f0;
  transform: translateX(4px);
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2) 0%, rgba(139, 92, 246, 0.2) 100%);
  color: #60a5fa;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 24px;
  background: linear-gradient(180deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 0 3px 3px 0;
}

.nav-text {
  font-size: 0.875rem;
  font-weight: 500;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 250px;
  transition: margin-left 0.3s ease;
  min-height: 100vh;
}

.sidebar.collapsed + .main-content {
  margin-left: 72px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: var(--bg-card);
  box-shadow: var(--shadow-sm);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.theme-toggle {
  width: 40px;
  height: 40px;
  background: var(--bg-secondary);
  border: none;
  border-radius: 10px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.theme-toggle:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.theme-toggle svg {
  width: 20px;
  height: 20px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 8px 6px 6px;
  background: var(--bg-secondary);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-menu:hover {
  background: var(--bg-hover);
}

.user-avatar-wrapper {
  position: relative;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--accent-color);
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent-color) 0%, #8b5cf6 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 14px;
  font-weight: 600;
  border: 2px solid var(--accent-color);
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
}

.user-role {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.user-dropdown {
  position: absolute;
  right: 24px;
  top: calc(100% + 8px);
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: var(--shadow-lg);
  padding: 8px;
  min-width: 160px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.2s ease;
  z-index: 50;
}

.user-menu:hover .user-dropdown {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-btn {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: none;
  border: none;
  border-radius: 8px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.875rem;
}

.dropdown-btn:hover {
  background: var(--bg-secondary);
  color: var(--error-color);
}

.dropdown-btn svg {
  width: 16px;
  height: 16px;
}

.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  
  .sidebar.collapsed {
    transform: translateX(0);
    width: 72px;
  }
  
  .main-content {
    margin-left: 0;
  }
}
</style>