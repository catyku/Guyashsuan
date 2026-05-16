<template>
  <header class="app-header">
    <div class="header-left">
      <el-button :icon="IconMenu2" circle plain @click="$emit('toggle-menu')" />
      <span class="system-name">古雅軒法律事務所 - 後台管理</span>
    </div>
    <div class="header-right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <IconUser :size="18" />
          <span>{{ authStore.loginName }}</span>
          <IconChevronDown :size="16" />
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="changePwd">
              <IconKey :size="16" />
              修改密碼
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <IconLogout :size="16" />
              登出
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
  <ChangePwdDialog v-model:visible="showChangePwd" />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  IconMenu2,
  IconUser,
  IconChevronDown,
  IconKey,
  IconLogout,
} from '@tabler/icons-vue'
import { useAuthStore } from '@/stores/auth'
import ChangePwdDialog from '@/components/ChangePwdDialog.vue'

defineEmits(['toggle-menu'])

const router = useRouter()
const authStore = useAuthStore()
const showChangePwd = ref(false)

async function handleCommand(cmd: string) {
  if (cmd === 'changePwd') {
    showChangePwd.value = true
  } else if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm('確定要登出嗎？', '確認', {
        confirmButtonText: '登出',
        cancelButtonText: '取消',
        type: 'warning',
      })
    } catch {
      return
    }
    await authStore.logout()
    router.push({ name: 'Login' })
  }
}
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 16px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.system-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #303133;
  font-size: 14px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}
.user-info:hover {
  background: #f5f7fa;
}
</style>