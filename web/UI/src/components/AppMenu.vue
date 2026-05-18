<template>
  <el-drawer :model-value="open" :with-header="false" direction="ltr" size="220px"
    @update:model-value="$emit('update:open', $event)">
    <div class="menu-container">
      <div class="menu-logo">後台選單</div>
      <el-menu :default-active="activeMenu" router @select="$emit('update:open', false)">
        <el-menu-item index="/index.html">
          <IconHome :size="18" />
          <span>首頁概覽</span>
        </el-menu-item>
        <!--<el-menu-item index="/attorney.html">
          <IconGavel :size="18" />
          <span>律師維護</span>
        </el-menu-item>
        <el-menu-item index="/service.html">
          <IconBriefcase :size="18" />
          <span>業務領域</span>
        </el-menu-item>-->
        <el-menu-item index="/case.html">
          <IconScale :size="18" />
          <span>案件實績</span>
        </el-menu-item>
        <el-menu-item index="/share.html">
          <IconNotes :size="18" />
          <span>情報分享</span>
        </el-menu-item>
        <!--<el-menu-item index="/consultation.html">
          <IconMessage :size="18" />
          <span>法律諮詢</span>
        </el-menu-item>
        <el-menu-item index="/banner.html">
          <IconPhoto :size="18" />
          <span>輪播管理</span>
        </el-menu-item>
        <el-menu-item index="/site.html">
          <IconSettings :size="18" />
          <span>網站設定</span>
        </el-menu-item>
        <el-menu-item index="/admin-user.html">
          <IconUsers :size="18" />
          <span>管理員</span>
        </el-menu-item>-->
      </el-menu>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  IconHome,

  IconScale,
  IconNotes,

} from '@tabler/icons-vue'

defineProps<{ open: boolean }>()
defineEmits(['update:open'])

const route = useRoute()
const activeMenu = computed(() => {
  const path = route.path
  return path.endsWith('.html') ? path : path + '.html'
})
</script>

<style scoped>
.menu-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.menu-logo {
  padding: 16px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
}
.el-menu {
  border-right: none;
  flex: 1;
}
.el-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>