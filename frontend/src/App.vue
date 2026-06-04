<script setup lang="ts">
import { RouterView } from 'vue-router'
import { initTheme } from './utils/theme'

// 初始化主题
initTheme()
</script>

<template>
  <div class="app-container">
    <RouterView />
  </div>
</template>

<style>
/* ═══════════════════════════════════════════
   Inkwell — Editorial Elegance Design System
   A refined, magazine-inspired CMS aesthetic
   ═══════════════════════════════════════════ */

@import url('https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:ital,opsz,wght@0,9..40,100..1000;1,9..40,100..1000&family=Noto+Serif+SC:wght@400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

body {
  font-family: 'DM Sans', 'Noto Serif SC', -apple-system, BlinkMacSystemFont, sans-serif;
  transition: background-color 0.4s cubic-bezier(0.4, 0, 0.2, 1),
              color 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* ─── Light Theme: Warm Ivory ─── */
:root.light {
  --bg-primary: #FAF7F2;
  --bg-secondary: #F3EDE4;
  --bg-tertiary: #EBE3D7;
  --bg-card: #FFFFFF;
  --bg-hover: #F5EFE6;
  --bg-inset: #F0EAE0;
  --text-primary: #1A1612;
  --text-secondary: #5C534A;
  --text-muted: #9C9189;
  --border-color: #E2D9CE;
  --border-light: #EDE6DC;
  --accent-color: #C45D3E;
  --accent-hover: #A84B30;
  --accent-soft: rgba(196, 93, 62, 0.08);
  --accent-muted: rgba(196, 93, 62, 0.15);
  --success-color: #3D8B5E;
  --success-soft: rgba(61, 139, 94, 0.1);
  --warning-color: #C49A2A;
  --warning-soft: rgba(196, 154, 42, 0.1);
  --error-color: #C44B3E;
  --error-soft: rgba(196, 75, 62, 0.08);
  --shadow-xs: 0 1px 2px rgba(26, 22, 18, 0.04);
  --shadow-sm: 0 2px 8px rgba(26, 22, 18, 0.06);
  --shadow-md: 0 4px 16px rgba(26, 22, 18, 0.08);
  --shadow-lg: 0 8px 32px rgba(26, 22, 18, 0.10);
  --shadow-xl: 0 16px 48px rgba(26, 22, 18, 0.12);
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 14px;
  --radius-xl: 18px;
  --font-display: 'DM Serif Display', 'Noto Serif SC', Georgia, serif;
  --font-body: 'DM Sans', 'Noto Serif SC', sans-serif;
  --sidebar-bg: #1A1612;
  --sidebar-text: #D4C8BA;
  --sidebar-hover: rgba(196, 93, 62, 0.12);
  --sidebar-active: rgba(196, 93, 62, 0.18);
}

/* ─── Dark Theme: Deep Charcoal ─── */
:root.dark {
  --bg-primary: #141210;
  --bg-secondary: #1C1A17;
  --bg-tertiary: #252320;
  --bg-card: #1C1A17;
  --bg-hover: #252320;
  --bg-inset: #1A1816;
  --text-primary: #EDE6DC;
  --text-secondary: #A89E94;
  --text-muted: #6B6259;
  --border-color: #2E2A26;
  --border-light: #252220;
  --accent-color: #E07A5F;
  --accent-hover: #C45D3E;
  --accent-soft: rgba(224, 122, 95, 0.1);
  --accent-muted: rgba(224, 122, 95, 0.18);
  --success-color: #5DAE7E;
  --success-soft: rgba(93, 174, 126, 0.12);
  --warning-color: #D4A83A;
  --warning-soft: rgba(212, 168, 58, 0.12);
  --error-color: #E06B5E;
  --error-soft: rgba(224, 107, 94, 0.1);
  --shadow-xs: 0 1px 2px rgba(0, 0, 0, 0.2);
  --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.25);
  --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.3);
  --shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.35);
  --shadow-xl: 0 16px 48px rgba(0, 0, 0, 0.4);
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 14px;
  --radius-xl: 18px;
  --font-display: 'DM Serif Display', 'Noto Serif SC', Georgia, serif;
  --font-body: 'DM Sans', 'Noto Serif SC', sans-serif;
  --sidebar-bg: #0E0D0C;
  --sidebar-text: #A89E94;
  --sidebar-hover: rgba(224, 122, 95, 0.1);
  --sidebar-active: rgba(224, 122, 95, 0.16);
}

body {
  background-color: var(--bg-primary);
  color: var(--text-primary);
}

/* ─── Form Elements ─── */
input, textarea, select {
  font-family: var(--font-body);
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 1.5px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 0.625rem 0.875rem;
  font-size: 0.875rem;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

input:focus, textarea:focus, select:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 3px var(--accent-soft);
  background-color: var(--bg-card);
}

input::placeholder, textarea::placeholder {
  color: var(--text-muted);
}

/* ─── Buttons ─── */
button {
  font-family: var(--font-body);
  font-size: 0.875rem;
  font-weight: 500;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ─── Card ─── */
.card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  transition: box-shadow 0.3s ease, transform 0.3s ease, border-color 0.3s ease;
}

.card:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--border-color);
}

/* ─── Typography ─── */
h1, h2, h3 {
  font-family: var(--font-display);
  letter-spacing: -0.01em;
}

/* ─── Animations ─── */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slideInLeft {
  from { opacity: 0; transform: translateX(-16px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes scaleIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-16px) rotate(2deg); }
}

@keyframes grain {
  0%, 100% { transform: translate(0, 0); }
  10% { transform: translate(-5%, -10%); }
  30% { transform: translate(3%, -15%); }
  50% { transform: translate(-15%, 5%); }
  70% { transform: translate(8%, 10%); }
  90% { transform: translate(-10%, 15%); }
}

.animate-fade-in { animation: fadeIn 0.4s cubic-bezier(0.4, 0, 0.2, 1) both; }
.animate-fade-in-up { animation: fadeInUp 0.5s cubic-bezier(0.4, 0, 0.2, 1) both; }
.animate-slide-in { animation: slideInLeft 0.4s cubic-bezier(0.4, 0, 0.2, 1) both; }
.animate-scale-in { animation: scaleIn 0.35s cubic-bezier(0.4, 0, 0.2, 1) both; }
.animate-pulse { animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite; }

/* Stagger delays for children */
.stagger > *:nth-child(1) { animation-delay: 0ms; }
.stagger > *:nth-child(2) { animation-delay: 60ms; }
.stagger > *:nth-child(3) { animation-delay: 120ms; }
.stagger > *:nth-child(4) { animation-delay: 180ms; }
.stagger > *:nth-child(5) { animation-delay: 240ms; }
.stagger > *:nth-child(6) { animation-delay: 300ms; }
.stagger > *:nth-child(7) { animation-delay: 360ms; }
.stagger > *:nth-child(8) { animation-delay: 420ms; }
.stagger > *:nth-child(9) { animation-delay: 480ms; }
.stagger > *:nth-child(10) { animation-delay: 540ms; }

/* ─── Scrollbar ─── */
::-webkit-scrollbar {
  width: 5px;
  height: 5px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: var(--text-muted);
  border-radius: 10px;
  opacity: 0.5;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--text-secondary);
}

/* ─── Selection ─── */
::selection {
  background: var(--accent-muted);
  color: var(--text-primary);
}

/* ─── Focus Visible ─── */
:focus-visible {
  outline: 2px solid var(--accent-color);
  outline-offset: 2px;
}

.app-container {
  min-height: 100vh;
}
</style>
