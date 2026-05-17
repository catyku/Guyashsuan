<template>
  <div>
    <div class="page-header"><h2>網站設定</h2></div>
    <el-form :model="form" label-width="120px" v-loading="loading">
      <el-card v-for="item in form" :key="item.site_key" class="setting-card">
        <el-form-item :label="item.remark || item.site_key">
          <el-input v-model="item.site_value" type="textarea" :rows="isLongText(item.site_key) ? 4 : 1" />
          <span class="field-key">{{ item.site_key }}</span>
        </el-form-item>
      </el-card>
      <el-form-item>
        <el-button type="primary" @click="handleSave" :loading="saving">儲存設定</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '@/utils/http'

const form = ref<any[]>([])
const loading = ref(false)
const saving = ref(false)

function isLongText(key: string) {
  return ['description', 'service_time'].includes(key)
}

async function loadData() {
  loading.value = true
  try {
    const res = await http.get('/site')
    form.value = res.data || []
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  saving.value = true
  try {
    for (const item of form.value) {
      await http.put(`/site/${item.site_key}`, {
        siteValue: item.site_value,
        remark: item.remark,
      })
    }
    ElMessage.success('儲存成功')
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h2 { margin: 0; }
.setting-card { margin-bottom: 12px; }
.field-key { font-size: 12px; color: #909399; margin-left: 8px; }
</style>