<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="login-title">古雅軒法律事務所</h1>
      <p class="login-subtitle">後台管理系統</p>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="acct">
          <el-input v-model="form.acct" placeholder="帳號" :prefix-icon="IconUser" size="large" />
        </el-form-item>
        <el-form-item prop="pwd">
          <el-input v-model="form.pwd" type="password" placeholder="密碼" :prefix-icon="IconLock" size="large"
            show-password />
        </el-form-item>
        <el-form-item prop="captcha">
          <div class="captcha-row">
            <el-input v-model="form.captcha" placeholder="驗證碼" size="large" class="captcha-input" />
            <img :src="captchaUrl" @click="refreshCaptcha" class="captcha-img" alt="驗證碼" title="點擊刷新" />
          </div>
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading" size="large" class="login-btn">
          登入
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { IconUser, IconLock } from '@tabler/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const captchaUrl = ref('')

const form = ref({
  acct: '',
  pwd: '',
  captcha: '',
})

const rules: FormRules = {
  acct: [{ required: true, message: '請輸入帳號', trigger: 'blur' }],
  pwd: [{ required: true, message: '請輸入密碼', trigger: 'blur' }],
  captcha: [{ required: true, message: '請輸入驗證碼', trigger: 'blur' }],
}

function refreshCaptcha() {
  captchaUrl.value = `/api/captcha?t=${Date.now()}`
}

onMounted(() => {
  refreshCaptcha()
})

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await authStore.login(form.value.acct, form.value.pwd, form.value.captcha)
    const redirect = (route.query.redirect as string) || '/index.html'
    router.push(redirect)
  } catch {
    refreshCaptcha()
    form.value.captcha = ''
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a237e, #283593);
}
.login-card {
  width: 380px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}
.login-title {
  text-align: center;
  font-size: 22px;
  color: #1a237e;
  margin-bottom: 4px;
}
.login-subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 24px;
  font-size: 14px;
}
.captcha-row {
  display: flex;
  gap: 8px;
  width: 100%;
}
.captcha-input {
  flex: 1;
}
.captcha-img {
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
}
.login-btn {
  width: 100%;
}
</style>