// src/utils/theme.ts
import { ref, watch } from 'vue'

type Theme = 'light' | 'dark'

const theme = ref<Theme>('light')

// 应用主题到DOM
const applyTheme = (currentTheme: Theme) => {
    document.documentElement.classList.remove('light', 'dark')
    document.documentElement.classList.add(currentTheme)
    localStorage.setItem('theme', currentTheme)
}

// 初始化主题
const initTheme = () => {
    // 优先从localStorage读取
    const saved = localStorage.getItem('theme') as Theme
    if (saved) {
        theme.value = saved
    } else {
        // 检测系统主题
        if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
            theme.value = 'dark'
        }
    }
    applyTheme(theme.value)
}

// 切换主题
const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
}

// 监听主题变化
watch(theme, (newTheme) => {
    applyTheme(newTheme)
})

export { theme, toggleTheme, initTheme }
