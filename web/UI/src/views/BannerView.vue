<template>
  <div>
    <div class="page-header">
      <h2>輪播管理</h2>
      <el-button type="primary" @click="openDialog(null)"><IconPlus :size="18" /> 新增輪播</el-button>
    </div>
    <el-table :data="items" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="標題" width="200" />
      <el-table-column prop="subtitle" label="副標題" width="200" show-overflow-tooltip />
      <el-table-column label="圖片" width="120">
        <template #default="{row}">
          <img v-if="row.image" :src="row.image" style="max-width:100px;max-height:60px;border-radius:4px" />
        </template>
      </el-table-column>
      <el-table-column prop="linkUrl" label="連結" width="200" show-overflow-tooltip />
      <el-table-column prop="isShow" label="顯示" width="70" align="center">
        <template #default="{row}"><el-tag :type="row.is_show==='Y'?'success':'info'" size="small">{{row.is_show==='Y'?'是':'否'}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">編輯</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId?'編輯輪播':'新增輪播'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="標題"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="副標題"><el-input v-model="form.subtitle" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="圖片" required>
          <el-upload v-if="editingId" :action="uploadUrl" :headers="uploadHeaders" :on-success="handleUploadSuccess" :show-file-list="false" accept="image/*">
            <el-button size="small">選擇圖片</el-button>
          </el-upload>
          <div v-if="form.image" class="preview-row"><img :src="form.image" class="preview-img" /></div>
        </el-form-item>
        <el-form-item label="連結"><el-input v-model="form.linkUrl" placeholder="https://..." /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="顯示"><el-switch v-model="form.isShow" active-value="Y" inactive-value="N" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave" :loading="saving">儲存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IconPlus } from '@tabler/icons-vue'
import http from '@/utils/http'

const items=ref<any[]>([])
const dialogVisible=ref(false),editingId=ref<number|null>(null),saving=ref(false)

const uploadUrl=computed(()=>`/api/banner/${editingId.value||0}/photo`)
const uploadHeaders=computed(()=>{const t=document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];return t?{'X-CSRF-TOKEN':decodeURIComponent(t)}:{}})

const form=ref({title:'',subtitle:'',image:'',linkUrl:'',sortOrder:0,isShow:'Y'})

async function loadData(){const res=await http.get('/banner');items.value=res.data||[]}
function openDialog(row:any|null){
  if(row){editingId.value=row.id;form.value={...row,linkUrl:row.link_url||'',sortOrder:row.sort_order??0,isShow:row.is_show||'Y'}}
  else{editingId.value=null;form.value={title:'',subtitle:'',image:'',linkUrl:'',sortOrder:0,isShow:'Y'}}
  dialogVisible.value=true
}
async function handleSave(){
  saving.value=true;try{
    if(editingId.value)await http.put(`/banner/${editingId.value}`,form.value)
    else await http.post('/banner',form.value)
    ElMessage.success('儲存成功');dialogVisible.value=false;await loadData()
  }finally{saving.value=false}
}
async function handleDelete(row:any){await ElMessageBox.confirm('確定刪除此輪播？','確認',{type:'warning'});await http.delete(`/banner/${row.id}`);ElMessage.success('刪除成功');await loadData()}
function handleUploadSuccess(res:any){if(res.code==='OK'){form.value.image=res.path;ElMessage.success('上傳成功')}}
onMounted(loadData)
</script>
<style scoped>
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}
.page-header h2{margin:0}
.preview-row{margin-top:8px}
.preview-img{max-width:200px;max-height:150px;border-radius:4px}
</style>