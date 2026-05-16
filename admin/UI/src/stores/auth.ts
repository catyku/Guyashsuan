import { defineStore } from 'pinia'
import { ref } from 'vue'
import http from '@/utils/http'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', () => {
  const loginName = ref<string>('')
  const isLoggedIn = ref<boolean>(false)

  async function login(acct: string, pwd: string, captcha: string) {
    try {
      const res = await http.post('/auth/login', { acct, pwd, captcha })
      loginName.value = res.data.name
      isLoggedIn.value = true

      try {
        await http.get('/auth/me', { loading: false } as any)
      } catch {
        // 不中斷流程
      }

      ElMessage.success('登入成功')
      return res.data
    } catch (error: any) {
      throw error
    }
  }

  async function logout() {
    try {
      await http.post('/auth/logout')
    } catch {
      // 即使登出 API 失敗也繼續清理
    }
    clearAuth()
    ElMessage.success('已登出')
  }

  async function fetchMe() {
    try {
      const res = await http.get('/auth/me')
      loginName.value = res.data.name
      isLoggedIn.value = true
    } catch {
      isLoggedIn.value = false
      loginName.value = ''
    }
  }

  function clearAuth() {
    loginName.value = ''
    isLoggedIn.value = false
  }

  return { loginName, isLoggedIn, login, logout, fetchMe, clearAuth }
}, {
  persist: {
    key: 'law-auth',
    storage: localStorage,
    pick: ['loginName', 'isLoggedIn'],
  },
})