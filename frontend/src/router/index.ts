import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue')
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        children: [
            {
                path: 'articles',
                name: 'ArticleList',
                component: () => import('@/views/ArticleList.vue')
            },
            {
                path: 'categories',
                name: 'CategoryList',
                component: () => import('@/views/CategoryList.vue')
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('@/views/Profile.vue')
            }
        ]
    },
    {
        path: '/article/add',
        name: 'AddArticle',
        component: () => import('@/views/AddArticle.vue')
    },
    {
        path: '/article/edit/:id',
        name: 'EditArticle',
        component: () => import('@/views/EditArticle.vue')
    },
    {
        path: '/category/add',
        name: 'AddCategory',
        component: () => import('@/views/AddCategory.vue')
    },
    {
        path: '/category/edit/:id',
        name: 'EditCategory',
        component: () => import('@/views/EditCategory.vue')
    },
    {
        path: '/article/crawl',
        name: 'CrawlArticle',
        component: () => import('@/views/CrawlArticle.vue')
    },
    {
        path: '/article/detail/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/ArticleDetail.vue')
    }

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to) => {
    const token = localStorage.getItem('token')
    if (to.path !== '/login' && to.path !== '/register' && !token) {
        return '/login'
    }
})


export default router