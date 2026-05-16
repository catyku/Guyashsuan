<template>
  <footer class="app-footer">
    <p>&copy; {{ year }} 古雅軒法律事務所 後台管理系統</p>
  </footer>
</template>

<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<style scoped>
.app-footer {
  text-align: center;
  padding: 16px;
  color: #909399;
  font-size: 12px;
  border-top: 1px solid #e4e7ed;
}
</style>