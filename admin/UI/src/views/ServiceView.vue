<template>
  <div class="service-view">
    <div class="page-header">
      <h2>業務領域管理</h2>
      <el-button type="primary" @click="openDialog(null)">
        <IconPlus :size="18" /> 新增業務
      </el-button>
    </div>
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜尋業務名稱" clearable @clear="loadData" @keyup.enter="loadData"
        style="width: 300px" />
      <el-button @click="loadData">搜尋</el-button>
    </div>
    <el-table :data="items" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="業務名稱" width="150" />
      <el-table-column prop="nameEn" label="英文名稱" width="180" />
      <el-table-column prop="icon" label="圖示" width="120" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="isShow" label="顯示" width="70" align="center">
        <template #default="{ row }">
          <el-tag :type="row.is_show === 'Y' ? 'success' : 'info'" size="small">
            {{ row.is_show === 'Y' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">編輯</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-row">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="total, prev, pager, next"
        @current-change="loadData" />
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '編輯業務' : '新增業務'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名稱" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="英文名稱">
          <el-input v-model="form.nameEn" />
        </el-form-item>
        <el-form-item label="圖示">
          <el-input v-model="form.icon" placeholder="例：fas fa-balance-scale" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="顯示">
          <el-switch v-model="form.isShow" active-value="Y" inactive-value="N" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">儲存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IconPlus } from '@tabler/icons-vue'
import http from '@/utils/http'

const items = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const keyword = ref('')
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const saving = ref(false)

const form = ref({
  name: '', nameEn: '', icon: '', description: '', sortOrder: 0, isShow: 'Y',
})

async function loadData() {
  const res = await http.get('/service', {
    params: { keyword: keyword.value, page: page.value, size: size.value },
  })
  items.value = res.data.items || []
  total.value = res.data.total || 0
}

function openDialog(row: any | null) {
  if (row) {
    editingId.value = row.id
    form.value = { ...row, sortOrder: row.sort_order ?? 0, isShow: row.is_show ?? 'Y', nameEn: row.name_en ?? '' }
  } else {
    editingId.value = null
    form.value = { name: '', nameEn: '', icon: '', description: '', sortOrder: 0, isShow: 'Y' }
  }
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await http.put(`/service/${editingId.value}`, form.value)
    } else {
      await http.post('/service', form.value)
    }
    ElMessage.success('儲存成功')
    dialogVisible.value = false
    await loadData()
  } finally {
    saving.value = false
  }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`確定刪除「${row.name}」？`, '確認', { type: 'warning' })
  await http.delete(`/service/${row.id}`)
  ElMessage.success('刪除成功')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; }
.search-bar { display: flex; gap: 8px; margin-bottom: 16px; }
.pagination-row { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>