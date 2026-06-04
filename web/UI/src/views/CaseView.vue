<template>
  <div>
    <div class="page-header">
      <h2>案件實績</h2>
      <el-button type="primary" @click="openDialog(null)"><IconPlus :size="18" /> 新增案件</el-button>
    </div>
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜尋標題/內容" clearable @clear="loadData" @keyup.enter="loadData" style="width:300px" />
        <el-select v-model="categoryFilter" placeholder="類別" clearable @change="loadData" style="width:140px">
        <el-option label="民事" value="民事" /><el-option label="刑事" value="刑事" /><el-option label="家事" value="家事" /><el-option label="行政" value="行政" />
      </el-select>
      <el-button @click="loadData">搜尋</el-button>
    </div>
    <el-table :data="items" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="category" label="類別" width="80" />
      <el-table-column prop="title" label="標題" min-width="200" show-overflow-tooltip />
      <el-table-column prop="case_date" label="日期" width="110" />
      <el-table-column prop="is_show" label="顯示" width="70" align="center">
        <template #default="{ row }"><el-tag :type="row.is_show === 'Y' ? 'success' : 'info'" size="small">{{ row.is_show === 'Y' ? '是' : '否' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">編輯</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-row"><el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="total,prev,pager,next" @current-change="loadData" /></div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '編輯案件' : '新增案件'" width="900px" :close-on-click-modal="false" top="5vh">
      <el-form :model="form" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="類別" required>
              <el-select v-model="form.category" placeholder="選擇類別" style="width:100%">
                <el-option label="民事" value="民事" />
                <el-option label="刑事" value="刑事" />
                <el-option label="家事" value="家事" />
                <el-option label="行政" value="行政" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="日期">
              <el-date-picker v-model="form.caseDate" type="date" value-format="YYYY-MM-DD" placeholder="選擇日期" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="顯示">
              <el-switch v-model="form.isShow" active-value="Y" inactive-value="N" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="標題" required>
          <el-input v-model="form.title" placeholder="請輸入案件標題" />
        </el-form-item>
        <el-form-item label="內容">
          <div style="border:1px solid #ccc;border-radius:4px;">
            <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" style="border-bottom:1px solid #ccc" />
            <Editor v-model="form.content" :defaultConfig="editorConfig" style="height:350px;overflow-y:hidden" @onCreated="handleEditorCreated" />
          </div>
        </el-form-item>
        <el-form-item label="封面圖">
          <el-upload :action="editingId ? uploadUrl : `/api/upload/image?subDir=case`" :headers="uploadHeaders" :on-success="handlePhotoUploadSuccess" :show-file-list="false" accept="image/*" :before-upload="refreshCsrfToken">
            <el-button size="small">選擇圖片</el-button>
          </el-upload>
          <div v-if="form.image" class="preview-row"><img :src="getImageUrl(form.image)" class="preview-img" /></div>
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
import { ref, shallowRef, onMounted, computed, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IconPlus } from '@tabler/icons-vue'
import http from '@/utils/http'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const items = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const keyword = ref('')
const categoryFilter = ref('')
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const saving = ref(false)
const editorRef = shallowRef()

const uploadUrl = computed(() => `/api/case/${editingId.value || 0}/photo`)
const uploadHeaders = ref(getCsrfHeaders())

function getCsrfHeaders() {
  const t = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1]
  return t ? { 'X-CSRF-TOKEN': decodeURIComponent(t) } : {}
}

function refreshCsrfToken() {
  uploadHeaders.value = getCsrfHeaders()
}

const form = ref({ category: '', title: '', content: '', caseDate: '', image: '', isShow: 'Y' })

function getImageUrl(img: string): string {
  if (!img) return ''
  if (img.startsWith('http') || img.startsWith('data:')) return img
  if (img.startsWith('/uploads/')) return img
  if (img.startsWith('uploads/')) return '/' + img
  if (img.startsWith('/')) return '/uploads' + img
  return '/uploads/' + img
}

const toolbarConfig = {}
const editorConfig = {
  placeholder: '請輸入內容...',
  MENU_CONF: {
    uploadImage: {
      maxFileSize: 10 * 1024 * 1024,
      // 完全自定義上傳，確保 CSRF token 是最新的
      async customUpload(file: File, insertFn: Function) {
        const url = `/api/upload/image`
        const formData = new FormData()
        formData.append('file', file)
        // 每次上傳都重新取得 CSRF token
        refreshCsrfToken()
        const headers = { ...uploadHeaders.value }
        try {
          const res = await fetch(url, { method: 'POST', headers, body: formData })
          const json = await res.json()
          // 後端回傳格式: { errno: 0, data: { url: "/uploads/editor/xxx.jpg", alt: "filename" } }
          const raw = json?.data?.url || json?.location || json?.path || ''
          const imageUrl = getImageUrl(raw)
          insertFn(imageUrl, json?.data?.alt || '')
        } catch (e) {
          console.error('編輯器圖片上傳失敗:', e)
        }
      },
    },
  },
}

function handleEditorCreated(editor: any) {
  editorRef.value = editor
}

async function loadData() {
  const res = await http.get('/case', { params: { keyword: keyword.value, category: categoryFilter.value, page: page.value, size: size.value } })
  items.value = res.data.items || []
  total.value = res.data.total || 0
}

function openDialog(row: any | null) {
  if (row) {
    editingId.value = row.id
    form.value = { ...row, caseDate: row.case_date || '', isShow: row.is_show || 'Y', content: row.content || '' }
  } else {
    editingId.value = null
    form.value = { category: '', title: '', content: '', caseDate: '', image: '', isShow: 'Y' }
  }
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await http.put(`/case/${editingId.value}`, form.value)
    } else {
      await http.post('/case', form.value)
    }
    ElMessage.success('儲存成功')
    dialogVisible.value = false
    await loadData()
  } finally {
    saving.value = false
  }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`確定刪除「${row.title}」？`, '確認', { type: 'warning' })
  await http.delete(`/case/${row.id}`)
  ElMessage.success('刪除成功')
  await loadData()
}

function handlePhotoUploadSuccess(res: any) {
  // 編輯模式：/api/case/{id}/photo 回傳 { code: "OK", path: "/uploads/case/xxx.jpg" }
  if (res.code === 'OK' && res.path) {
    form.value.image = res.path
    ElMessage.success('上傳成功')
    return
  }
  // 新增模式：/api/upload/image 回傳 { errno: 0, data: { url: "/uploads/editor/xxx.jpg" } }
  if (res.errno === 0 && res.data?.url) {
    form.value.image = res.data.url
    ElMessage.success('上傳成功')
    return
  }
  ElMessage.error('上傳失敗')
}

onBeforeUnmount(() => {
  if (editorRef.value) {
    (editorRef.value as any).destroy()
  }
})

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.page-header h2 { margin: 0 }
.search-bar { display: flex; gap: 8px; margin-bottom: 16px }
.pagination-row { display: flex; justify-content: flex-end; margin-top: 16px }
.preview-row { margin-top: 8px }
.preview-img { max-width: 200px; max-height: 150px; border-radius: 4px }
</style>