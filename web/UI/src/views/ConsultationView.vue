<template>
  <div>
    <div class="page-header"><h2>法律諮詢管理</h2></div>
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜尋姓名/主題/內容" clearable @clear="loadData" @keyup.enter="loadData" style="width:300px" />
      <el-select v-model="statusFilter" placeholder="狀態" clearable @change="loadData" style="width:120px">
        <el-option label="待處理" value="P" /><el-option label="處理中" value="D" /><el-option label="已結案" value="C" />
      </el-select>
      <el-button @click="loadData">搜尋</el-button>
    </div>
    <el-table :data="items" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="phone" label="電話" width="130" />
      <el-table-column prop="email" label="Email" width="180" show-overflow-tooltip />
      <el-table-column prop="subject" label="主題" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="狀態" width="90" align="center">
        <template #default="{row}">
          <el-tag :type="row.status==='P'?'warning':row.status==='D'?'primary':'success'" size="small">
            {{row.status==='P'?'待處理':row.status==='D'?'處理中':'已結案'}}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="inptime" label="時間" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="openReply(row)">回覆</el-button>
          <el-button size="small" :type="row.status==='C'?'warning':''" @click="toggleStatus(row)">
            {{row.status==='C'?'重開':'結案'}}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-row"><el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="total,prev,pager,next" @current-change="loadData" /></div>

    <el-dialog v-model="replyVisible" title="回覆諮詢" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="姓名">{{ currentRow?.name }}</el-descriptions-item>
        <el-descriptions-item label="電話">{{ currentRow?.phone }}</el-descriptions-item>
        <el-descriptions-item label="Email">{{ currentRow?.email }}</el-descriptions-item>
        <el-descriptions-item label="主題">{{ currentRow?.subject }}</el-descriptions-item>
        <el-descriptions-item label="內容">{{ currentRow?.content }}</el-descriptions-item>
        <el-descriptions-item label="時間">{{ currentRow?.inptime }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="replyForm" label-width="80px" style="margin-top:16px">
        <el-form-item label="狀態">
          <el-select v-model="replyForm.status">
            <el-option label="待處理" value="P" /><el-option label="處理中" value="D" /><el-option label="已結案" value="C" />
          </el-select>
        </el-form-item>
        <el-form-item label="回覆" required>
          <el-input v-model="replyForm.reply" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="replyVisible=false">取消</el-button><el-button type="primary" @click="handleReply" :loading="saving">送出</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/http'

const items=ref<any[]>([])
const total=ref(0),page=ref(1),size=ref(20),keyword=ref(''),statusFilter=ref('')
const replyVisible=ref(false),saving=ref(false)
const currentRow=ref<any>(null)
const replyForm=ref({reply:'',status:'D'})

async function loadData(){
  const res=await http.get('/consultation',{params:{keyword:keyword.value,status:statusFilter.value,page:page.value,size:size.value}})
  items.value=res.data.items||[];total.value=res.data.total||0
}
function openReply(row:any){currentRow.value=row;replyForm.value={reply:row.reply||'',status:row.status||'D'};replyVisible.value=true}
async function handleReply(){
  saving.value=true;try{
    await http.put(`/consultation/${currentRow.value.id}/reply`,replyForm.value)
    ElMessage.success('回覆成功');replyVisible.value=false;await loadData()
  }finally{saving.value=false}
}
async function toggleStatus(row:any){
  const newStatus=row.status==='C'?'P':'C'
  await http.put(`/consultation/${row.id}/status`,{status:newStatus})
  ElMessage.success('狀態更新成功');await loadData()
}
async function handleDelete(row:any){await ElMessageBox.confirm(`確定刪除「${row.name}」的諮詢？`,'確認',{type:'warning'});await http.delete(`/consultation/${row.id}`);ElMessage.success('刪除成功');await loadData()}
onMounted(loadData)
</script>
<style scoped>
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}
.page-header h2{margin:0}
.search-bar{display:flex;gap:8px;margin-bottom:16px}
.pagination-row{display:flex;justify-content:flex-end;margin-top:16px}
</style>