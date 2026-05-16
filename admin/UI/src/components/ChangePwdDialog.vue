<template>
  <el-dialog v-model="dialogVisible" title="修改密碼" width="400px" :close-on-click-modal="false">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="舊密碼" prop="oldPwd">
        <el-input v-model="form.oldPwd" type="password" show-password placeholder="請輸入舊密碼" />
      </el-form-item>
      <el-form-item label="新密碼" prop="newPwd">
        <el-input v-model="form.newPwd" type="password" show-password placeholder="請輸入新密碼" />
      </el-form-item>
      <el-form-item label="確認密碼" prop="confirmPwd">
        <el-input v-model="form.confirmPwd" type="password" show-password placeholder="請再次輸入新密碼" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">確認</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import http from '@/utils/http'

const props = defineProps<{ visible: boolean }>()
const emit = defineEmits(['update:visible'])

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = ref({
  oldPwd: '',
  newPwd: '',
  confirmPwd: '',
})

const rules: FormRules = {
  oldPwd: [{ required: true, message: '請輸入舊密碼', trigger: 'blur' }],
  newPwd: [
    { required: true, message: '請輸入新密碼', trigger: 'blur' },
    { min: 6, message: '密碼至少6碼', trigger: 'blur' },
  ],
  confirmPwd: [
    { required: true, message: '請確認新密碼', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: (err?: Error) => void) => {
        if (value !== form.value.newPwd) {
          callback(new Error('兩次密碼不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}

watch(() => props.visible, (val) => {
  dialogVisible.value = val
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
  if (!val) {
    form.value = { oldPwd: '', newPwd: '', confirmPwd: '' }
    formRef.value?.resetFields()
  }
})

async function handleSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await http.post('/auth/change-password', {
      oldPwd: form.value.oldPwd,
      newPwd: form.value.newPwd,
    })
    ElMessage.success('密碼修改成功')
    dialogVisible.value = false
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>