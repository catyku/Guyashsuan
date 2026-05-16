<template>
  <div>
    <div class="page-header">
      <h2>管理員帳號</h2>
      <el-button type="primary" @click="openDialog(null)"><IconPlus :size="18" /> 新增管理員</el-button>
    </div>
    <el-table :data="items" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="帳號" width="120" />
      <el-table-column prop="displayName" label="顯示名稱" width="120" />
      <el-table-column prop="role" label="角色" width="100" />
      <el-table-column prop="isEnabled" label="啟用" width="70" align="center">
        <template #default="{row}">
          <el-tag :type="row.is_enabled==='Y'?'success':'danger'" size="small">{{row.is_enabled==='Y'?'是':'否'}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="inptime" label="建立時間" width="170" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">編輯</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId?'編輯管理員':'新增管理員'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="帳號" :required="!editingId">
          <el-input v-model="form.username" :disabled="!!editingId" />
        </el-form-item>
        <el-form-item v-if="!editingId" label="密碼" required>
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item v-if="editingId" label="新密碼">
          <el-input v-model="form.password" type="password" show-password placeholder="留空不修改" />
        </el-form-item>
        <el-form-item label="顯示名稱">
          <el-input v-model="form.displayName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="form.role" placeholder="ADMIN" />
        </el-form-item>
        <el-form-item label="啟用">
          <el-switch v-model="form.isEnabled" active-value="Y" inactive-value="N" />
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave" :loading="saving">儲存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IconPlus } from '@tabler/icons-vue'
import http from '@/utils/http'

const items=ref<any[]>([])
const dialogVisible=ref(false),editingId=ref<number|null>(null),saving=ref(false)

const form=ref({username:'',password:'',displayName:'',role:'ADMIN',isEnabled:'Y'})

async function loadData(){const res=await http.get('/admin-user');items.value=res.data||[]}
function openDialog(row:any|null){
  if(row){editingId.value=row.id;form.value={username:row.username,password:'',displayName:row.display_name||'',role:row.role||'ADMIN',isEnabled:row.is_enabled||'Y'}}
  else{editingId.value=null;form.value={username:'',password:'',displayName:'',role:'ADMIN',isEnabled:'Y'}}
  dialogVisible.value=true
}
async function handleSave(){
  saving.value=true;try{
    if(editingId.value){
      const body:any={displayName:form.value.displayName,role:form.value.role,isEnabled:form.value.isEnabled}
      if(form.value.password)body.password=form.value.password
      await http.put(`/admin-user/${editingId.value}`,body)
    }else{
      await http.post('/admin-user',form.value)
    }
    ElMessage.success('儲存成功');dialogVisible.value=false;await loadData()
  }finally{saving.value=false}
}
async function handleDelete(row:any){
  await ElMessageBox.confirm(`確定刪除「${row.username}」？`,'確認',{type:'warning'})
  await http.delete(`/admin-user/${row.id}`);ElMessage.success('刪除成功');await loadData()
}
onMounted(loadData)
</script>
<style scoped>
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}
.page-header h2{margin:0}
</style>