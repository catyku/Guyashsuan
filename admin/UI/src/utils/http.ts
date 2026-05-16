import axios, { type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'
import { ElLoading, ElMessage } from 'element-plus'
import router from '@/router'

declare module 'axios' {
  interface AxiosRequestConfig {
    loading?: boolean
  }
  interface InternalAxiosRequestConfig {
    loading?: boolean
  }
}

export interface ApiRequestConfig<D = unknown> extends AxiosRequestConfig<D> {
  loading?: boolean
}

let loadingInstance: ReturnType<typeof ElLoading.service> | null = null
let activeRequests = 0

const http = axios.create({
  baseURL: '/api',
  withCredentials: true,
  timeout: 15000,
})

axios.defaults.xsrfCookieName = 'XSRF-TOKEN'
axios.defaults.xsrfHeaderName = 'X-CSRF-TOKEN'

http.interceptors.request.use(
  (config) => {
    if (config.loading !== false && activeRequests === 0) {
      loadingInstance = ElLoading.service({
        lock: true,
        text: '載入中...',
        background: 'rgba(0, 0, 0, 0.2)',
      })
    }
    if (config.loading !== false) activeRequests += 1

    const token = getCookie('XSRF-TOKEN')
    if (token) {
      config.headers['X-CSRF-TOKEN'] = token
    }
    return config
  },
  (error) => {
    if ((error.config as InternalAxiosRequestConfig)?.loading !== false) {
      activeRequests = Math.max(activeRequests - 1, 0)
    }
    if (activeRequests === 0 && loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
    return Promise.reject(error)
  },
)

http.interceptors.response.use(
  (response) => {
    if (response.config.loading !== false) {
      activeRequests = Math.max(activeRequests - 1, 0)
    }
    if (activeRequests === 0 && loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
    return response
  },
  (error) => {
    if ((error.config as InternalAxiosRequestConfig)?.loading !== false) {
      activeRequests = Math.max(activeRequests - 1, 0)
    }
    if (activeRequests === 0 && loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }

    const status = error.response?.status
    const msg = error.response?.data?.msg

    if (status === 401) {
      import('@/stores/auth').then(({ useAuthStore }) => {
        useAuthStore().clearAuth()
        const currentPath = window.location.pathname + window.location.search
        router.push({ name: 'Login', query: { redirect: currentPath } })
      })
      ElMessage.error(msg || '登入已逾期，請重新登入')
    } else if (status === 403) {
      ElMessage.error(msg || '無存取權限')
    } else if (msg) {
      ElMessage.error(typeof msg === 'string' ? msg : JSON.stringify(msg))
    } else {
      ElMessage.error('系統錯誤，請稍後再試')
    }
    return Promise.reject(error)
  },
)

function getCookie(name: string): string | null {
  const match = document.cookie.match(new RegExp('(^|;\\s*)' + name + '=([^;]*)'))
  const value = match?.[2]
  return value ? decodeURIComponent(value) : null
}

export default http