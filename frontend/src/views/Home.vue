<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { userApi, type User } from '@/api'
import { theme, toggleTheme } from '../utils/theme'

const router = useRouter()
const route = useRoute()
const user = ref<User | null>(null)
const sidebarCollapsed = ref(false)
const dropdownOpen = ref(false)

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

const closeDropdown = () => {
  dropdownOpen.value = false
}
</script>

<template>
  <div class="home-layout">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-inner">
        <!-- Logo -->
        <div class="logo-area">
          <div class="logo-mark">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 19l7-7 3 3-7 7-3-3z"/>
              <path d="M18 13l-1.5-7.5L2 2l3.5 14.5L13 18l5-5z"/>
              <path d="M2 2l7.586 7.586"/>
              <circle cx="11" cy="11" r="2"/>
            </svg>
          </div>
          <transition name="fade">
            <div v-if="!sidebarCollapsed" class="logo-text">
              <span class="logo-name">Inkwell</span>
              <span class="logo-sub">内容管理</span>
            </div>
          </transition>
        </div>

        <!-- Collapse Toggle -->
        <button class="collapse-toggle" @click="sidebarCollapsed = !sidebarCollapsed">
          <svg :style="{ transform: sidebarCollapsed ? 'rotate(180deg)' : 'none' }" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
        </button>

        <!-- Navigation -->
        <nav class="nav-list">
          <a
            v-for="item in navItems"
            :key="item.path"
            :href="item.path"
            :class="['nav-link', { active: route.path === item.path }]"
            @click.prevent="router.push(item.path)"
          >
            <div class="nav-icon">
              <svg v-if="item.icon === 'article'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                <line x1="8" y1="7" x2="16" y2="7"/>
                <line x1="8" y1="11" x2="14" y2="11"/>
              </svg>
              <svg v-else-if="item.icon === 'category'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
              </svg>
              <svg v-else-if="item.icon === 'profile'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
            <transition name="fade">
              <span v-if="!sidebarCollapsed" class="nav-label">{{ item.name }}</span>
            </transition>
            <transition name="fade">
              <div v-if="!sidebarCollapsed && route.path === item.path" class="nav-indicator"/>
            </transition>
          </a>
        </nav>

        <!-- Bottom User -->
        <div class="sidebar-bottom">
          <div class="sidebar-user" @click="dropdownOpen = !dropdownOpen">
            <div class="user-avatar-sm">
              <img v-if="user?.userPic" :src="user.userPic" alt="" class="avatar-img" />
              <span v-else class="avatar-fallback">{{ user?.username?.charAt(0) || 'U' }}</span>
            </div>
            <transition name="fade">
              <div v-if="!sidebarCollapsed" class="user-meta">
                <span class="user-name-sm">{{ user?.nickname || user?.username }}</span>
                <span class="user-role-sm">管理员</span>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="main-area" :class="{ expanded: sidebarCollapsed }">
      <!-- Header -->
      <header class="top-bar">
        <div class="bar-left">
          <h1 class="page-title">
            {{ navItems.find(item => item.path === route.path)?.name || 'Inkwell' }}
          </h1>
          <span class="page-divider">/</span>
          <span class="page-subtitle">{{ navItems.find(item => item.path === route.path)?.name || '' }}</span>
        </div>
        <div class="bar-right">
          <button
            class="theme-btn"
            @click="toggleTheme"
            :title="theme === 'light' ? '深色模式' : '浅色模式'"
          >
            <svg v-if="theme === 'light'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
            </svg>
            <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="5"/>
              <line x1="12" y1="1" x2="12" y2="3"/>
              <line x1="12" y1="21" x2="12" y2="23"/>
              <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
              <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
              <line x1="1" y1="12" x2="3" y2="12"/>
              <line x1="21" y1="12" x2="23" y2="12"/>
              <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
              <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
            </svg>
          </button>

          <div class="header-user" @click="dropdownOpen = !dropdownOpen">
            <div class="header-avatar">
              <img v-if="user?.userPic" :src="user.userPic" alt="" class="avatar-img" />
              <span v-else class="avatar-fallback">{{ user?.username?.charAt(0) || 'U' }}</span>
            </div>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </div>

          <!-- Dropdown -->
          <transition name="dropdown">
            <div v-if="dropdownOpen" class="dropdown-menu" @click="closeDropdown">
              <button class="dropdown-item logout" @click="handleLogout">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                  <polyline points="16 17 21 12 16 7"/>
                  <line x1="21" y1="12" x2="9" y2="12"/>
                </svg>
                <span>退出登录</span>
              </button>
            </div>
          </transition>
        </div>
      </header>

      <!-- Page Content -->
      <div class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
}

/* ─── Sidebar ─── */
.sidebar {
  width: 260px;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  transition: width 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar-inner {
  height: 100%;
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  padding: 24px 0;
  position: relative;
  overflow: hidden;
}

/* Subtle grain texture overlay */
.sidebar-inner::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.03'/%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 0;
}

.sidebar-inner > * {
  position: relative;
  z-index: 1;
}

.sidebar.collapsed {
  width: 72px;
}

/* Logo */
.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px 24px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  margin-bottom: 8px;
}

.logo-mark {
  width: 36px;
  height: 36px;
  background: var(--accent-color);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 2px 12px rgba(196, 93, 62, 0.3);
}

.logo-text {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.logo-name {
  font-family: var(--font-display);
  font-size: 1.15rem;
  color: #F0EAE0;
  letter-spacing: -0.02em;
}

.logo-sub {
  font-size: 0.7rem;
  color: var(--sidebar-text);
  opacity: 0.6;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

/* Collapse Toggle */
.collapse-toggle {
  position: absolute;
  right: -13px;
  top: 28px;
  width: 26px;
  height: 26px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all 0.25s ease;
  z-index: 10;
}

.collapse-toggle:hover {
  background: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
  transform: scale(1.1);
}

.collapse-toggle svg {
  transition: transform 0.3s ease;
}

/* Navigation */
.nav-list {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 14px;
  border-radius: var(--radius-md);
  color: var(--sidebar-text);
  text-decoration: none;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.nav-link:hover {
  background: var(--sidebar-hover);
  color: #EDE6DC;
}

.nav-link.active {
  background: var(--sidebar-active);
  color: var(--accent-color);
}

.nav-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.nav-label {
  font-size: 0.875rem;
  font-weight: 500;
  white-space: nowrap;
}

.nav-indicator {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--accent-color);
  border-radius: 3px 0 0 3px;
}

/* Sidebar Bottom */
.sidebar-bottom {
  padding: 0 12px;
  border-top: 1px solid rgba(255,255,255,0.06);
  padding-top: 16px;
}

.sidebar-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.2s ease;
}

.sidebar-user:hover {
  background: var(--sidebar-hover);
}

.user-avatar-sm {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--accent-color);
  color: white;
  font-size: 0.8rem;
  font-weight: 600;
  border-radius: 50%;
}

.user-meta {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.user-name-sm {
  font-size: 0.8rem;
  font-weight: 500;
  color: #D4C8BA;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role-sm {
  font-size: 0.68rem;
  color: var(--sidebar-text);
  opacity: 0.5;
}

/* ─── Main Area ─── */
.main-area {
  flex: 1;
  margin-left: 260px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.main-area.expanded {
  margin-left: 72px;
}

/* ─── Top Bar ─── */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-light);
  position: sticky;
  top: 0;
  z-index: 50;
  backdrop-filter: blur(12px);
  background: rgba(from var(--bg-card) r g b / 0.85);
}

.bar-left {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 1.35rem;
  font-weight: 400;
  color: var(--text-primary);
}

.page-divider {
  color: var(--text-muted);
  font-weight: 300;
}

.page-subtitle {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.bar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
}

.theme-btn {
  width: 38px;
  height: 38px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.theme-btn:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
  border-color: var(--border-color);
}

.header-user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 10px 5px 5px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.header-user:hover {
  border-color: var(--border-color);
  background: var(--bg-hover);
}

.header-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  overflow: hidden;
}

/* ─── Dropdown ─── */
.dropdown-menu {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: 6px;
  min-width: 180px;
  z-index: 100;
}

.dropdown-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: none;
  border: none;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.15s ease;
  font-size: 0.85rem;
}

.dropdown-item:hover {
  background: var(--bg-secondary);
}

.dropdown-item.logout:hover {
  background: var(--error-soft);
  color: var(--error-color);
}

/* ─── Page Content ─── */
.page-content {
  flex: 1;
  padding: 28px 32px;
  overflow-y: auto;
}

/* ─── Transitions ─── */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.dropdown-enter-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.dropdown-leave-active {
  transition: all 0.15s cubic-bezier(0.4, 0, 0.2, 1);
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.96);
}

.page-enter-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.page-leave-active {
  transition: all 0.15s cubic-bezier(0.4, 0, 0.2, 1);
}
.page-enter-from {
  opacity: 0;
  transform: translateY(12px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ─── Responsive ─── */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  .sidebar.collapsed {
    transform: translateX(0);
    width: 72px;
  }
  .main-area {
    margin-left: 0;
  }
  .main-area.expanded {
    margin-left: 72px;
  }
  .top-bar {
    padding: 14px 20px;
  }
  .page-content {
    padding: 20px;
  }
}
</style>
