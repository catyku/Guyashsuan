<template>
  <div class="main-layout">
    <AppHeader @toggle-menu="drawerOpen = !drawerOpen" />
    <AppMenu v-model:open="drawerOpen" />
    <main class="main-content">
      <RouterView />
    </main>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import AppMenu from '@/components/AppMenu.vue'
import AppFooter from '@/components/AppFooter.vue'

const drawerOpen = ref(false)
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.main-content {
  flex: 1;
  padding: 20px;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
}
</style>