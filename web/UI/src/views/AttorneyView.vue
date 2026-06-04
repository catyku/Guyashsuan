<template>
  <div class="attorney-view">
    <div class="page-header">
      <h2>律師維護</h2>
      <el-button type="primary" @click="openDialog(null)">
        <IconPlus :size="18" /> 新增律師
      </el-button>
    </div>
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜尋姓名/專長" clearable @clear="loadData" @keyup.enter="loadData"
        style="width: 300px" />
      <el-button @click="loadData">搜尋</el-button>
    </div>
    <el-table :data="items" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="title" label="職稱" width="100" />
      <el-table-column prop="licenseNo" label="證書字號" width="160" />
      <el-table-column prop="specialty" label="專長" min-width="200" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '編輯律師' : '新增律師'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="職稱">
          <el-input v-model="form.title" placeholder="例：所長、律師" />
        </el-form-item>
        <el-form-item label="證書字號">
          <el-input v-model="form.licenseNo" />
        </el-form-item>
        <el-form-item label="專長">
          <el-input v-model="form.specialty" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="學歷">
          <el-input v-model="form.education" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="經歷">
          <el-input v-model="form.experience" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="簡介">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="照片">
          <el-upload :action="uploadUrl" :headers="uploadHeaders" :on-success="handleUploadSuccess"
            :before-upload="beforeUpload" :show-file-list="false" accept="image/*">
            <el-button size="small">選擇圖片</el-button>
          </el-upload>
          <div v-if="form.photo" class="preview-row">
            <img :src="form.photo" class="preview-img" />
          </div>
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
import { ref, onMounted, computed } from 'vue'
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

const uploadUrl = computed(() => `/api/attorney/${editingId.value || 0}/photo`)
const uploadHeaders = computed(() => {
  const token = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1]
  return token ? { 'X-CSRF-TOKEN': decodeURIComponent(token) } : {}
})

const form = ref({
  name: '', title: '', licenseNo: '', photo: '', specialty: '',
  education: '', experience: '', description: '', sortOrder: 0, isShow: 'Y',
})

async function loadData() {
  const res = await http.get('/attorney', {
    params: { keyword: keyword.value, page: page.value, size: size.value },
  })
  items.value = res.data.items || []
  total.value = res.data.total || 0
}

function openDialog(row: any | null) {
  if (row) {
    editingId.value = row.id
    form.value = { ...row, sortOrder: row.sort_order || 0, isShow: row.is_show || 'Y' }
  } else {
    editingId.value = null
    form.value = { name: '', title: '', licenseNo: '', photo: '', specialty: '', education: '', experience: '', description: '', sortOrder: 0, isShow: 'Y' }
  }
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await http.put(`/attorney/${editingId.value}`, form.value)
    } else {
      await http.post('/attorney', form.value)
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
  await http.delete(`/attorney/${row.id}`)
  ElMessage.success('刪除成功')
  await loadData()
}

function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) ElMessage.error('只能上傳圖片')
  return isImage
}

function handleUploadSuccess(response: any) {
  if (response.code === 'OK') {
    form.value.photo = response.path
    ElMessage.success('上傳成功')
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h2 { margin: 0; }
.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.preview-row { margin-top: 8px; }
.preview-img { max-width: 200px; max-height: 150px; border-radius: 4px; }
</style>