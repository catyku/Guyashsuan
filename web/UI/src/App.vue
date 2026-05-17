<template>
  <RouterView v-if="isPublicRoute" />
  <MainLayout v-else />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const route = useRoute()
const isPublicRoute = computed(() => route.meta.public === true)
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}
body {
  font-family: 'Microsoft JhengHei', Arial, sans-serif;
  background: #f5f7fa;
}
</style>