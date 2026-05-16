import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), 'VITE_')
  const contextPath = env.VITE_CONTEXT_PATH || '/admin/'

  return {
    base: contextPath,
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    build: {
      outDir: '../src/main/resources/static/admin',
      emptyOutDir: true,
      chunkSizeWarningLimit: 1000,
    },
    server: {
      port: 5380,
      proxy: {
        '/api': {
          target: 'http://localhost:8092',
          changeOrigin: true,
        },
        '/captcha': {
          target: 'http://localhost:8092',
          changeOrigin: true,
        },
        '/uploads': {
          target: 'http://localhost:8092',
          changeOrigin: true,
        },
      },
    },
  }
})