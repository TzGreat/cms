import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios'

const instance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

const api = {
  get: <T>(url: string, config?: AxiosRequestConfig) =>
    instance.get<T>(url, config).then(res => res as T),
  post: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    instance.post<T>(url, data, config).then(res => res as T),
  put: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    instance.put<T>(url, data, config).then(res => res as T),
  patch: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    instance.patch<T>(url, data, config).then(res => res as T),
  delete: <T>(url: string, config?: AxiosRequestConfig) =>
    instance.delete<T>(url, config).then(res => res as T)
}

export interface Result<T = unknown> {
  code: number
  message: string
  data: T
}

export interface User {
  id: number
  username: string
  nickname: string
  email: string
  userPic: string
  createTime: string
  updateTime: string
}

export interface Category {
  id: number
  categoryName: string
  categoryAlias: string
  createUser: number
  createTime: string
  updateTime: string
}

export interface Article {
  id: number
  title: string
  content: string
  coverImg: string
  state: string
  categoryId: number
  createUser: number
  createTime: string
  updateTime: string
  summary: string
}

export interface PageBean<T> {
  total: number
  items: T[]
}

export const userApi = {
  register: (username: string, password: string) =>
    api.post<Result>('/user/register', null, { params: { username, password } }),

  login: (username: string, password: string) =>
    api.post<Result<string>>('/user/login', { username, password }),

  getUserInfo: () =>
    api.get<Result<User>>('/user/userInfo'),

  updateUser: (user: Partial<User>) =>
    api.put<Result>('/user/update', user),

  updateAvatar: (avatarUrl: string) =>
    api.patch<Result>('/user/updateAvatar', null, { params: { avatarUrl } }),

  updatePassword: (oldPwd: string, newPwd: string, rePwd: string) =>
    api.patch<Result>('/user/updatePwd', { old_pwd: oldPwd, new_pwd: newPwd, re_pwd: rePwd })
}

export const categoryApi = {
  add: (category: Omit<Category, 'id' | 'createUser' | 'createTime' | 'updateTime'>) =>
    api.post<Result>('/category', category),

  list: () =>
    api.get<Result<Category[]>>('/category'),

  getById: (id: number) =>
    api.get<Result<Category>>('/category/detail', { params: { id } }),

  update: (category: Partial<Category>) =>
    api.put<Result>('/category', category),

  delete: (id: number) =>
    api.delete<Result>(`/category/${id}`)
}

export const articleApi = {
  add: (article: Omit<Article, 'id' | 'createUser' | 'createTime' | 'updateTime' | 'summary'>) =>
    api.post<Result>('/article', article),

  list: (pageNum: number, pageSize: number, categoryId?: number, state?: string) =>
    api.get<Result<PageBean<Article>>>('/article', { params: { pageNum, pageSize, categoryId, state } }),

  getById: (id: number) =>
    api.get<Result<Article>>('/article/detail', { params: { id } }),

  update: (article: Partial<Article>) =>
    api.put<Result>('/article/info', article),

  delete: (id: number) =>
    api.delete<Result>('/article/info', { params: { id } })
}

export const aiApi = {
  /**
   * 获取文章摘要
   * @param articleId 文章ID
   */
  getSummary: (articleId: number) =>
    api.get<Result<string>>('/ai/summary', { params: { articleId } }),

  /**
   * 刷新文章摘要（强制重新生成）
   * @param articleId 文章ID
   */
  refreshSummary: (articleId: number) =>
    api.post<Result<string>>('/ai/summary', null, { params: { articleId } }),

  /**
   * 健康检查
   */
  health: () =>
    api.get<Result<string>>('/ai/health')
}

export const ocrApi = {
  /**
   * 图片文字识别
   * @param file 图片文件
   */
  recognize: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post<Result<string>>('/ocr/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const crawlerApi = {
  fetch: (url: string) =>
    api.post<Result<{
      title: string
      content: string
      coverImg: string
      sourceUrl: string
      sourceType: string
    }>>('/api/crawler/fetch', { url }),

  save: (url: string, categoryId?: number) =>
    api.post<Result>('/api/crawler/save', { url, categoryId })
}

export default instance