<template>
  <div class="home-view">
    <h2>系統概覽</h2>
    <el-row :gutter="20" class="stats-row">
      <el-col :span="12" v-for="stat in stats" :key="stat.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ color: stat.color }">
            <component :is="stat.icon" :size="32" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import http from '@/utils/http'
import { IconGavel, IconScale, IconNotes } from '@tabler/icons-vue'

interface StatItem {
  label: string
  value: number
  icon: any
  color: string
}

const stats = ref<StatItem[]>([])

onMounted(async () => {
  try {
    const res = await http.get('/dashboard/stats', { loading: false } as any)
    const data = res.data
    stats.value = [
      { label: '案件實績', value: data.caseCount || 0, icon: IconScale, color: '#67C23A' },
      { label: '情報分享', value: data.shareCount || 0, icon: IconNotes, color: '#E6A23C' },
    ]
  } catch {
    // ignore
  }
})
</script>

<style scoped>
.home-view h2 {
  margin-bottom: 20px;
  color: #303133;
}
.stats-row {
  margin-bottom: 20px;
}
.stat-card {
  display: flex;
  align-items: center;
  padding: 8px;
}
.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  width: 100%;
}
.stat-icon {
  flex-shrink: 0;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
</style>