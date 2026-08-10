<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="admin-brand">
        <span class="brand-mark">EV</span>
        <div>
          <strong>管理后台</strong>
          <small>学习演示版本</small>
        </div>
      </div>
      <nav class="side-nav">
        <router-link to="/admin">工作台</router-link>
        <router-link v-for="item in moduleMenus" :key="item.key" :to="`/admin/${item.key}`">
          <span>{{ item.icon }}</span>{{ item.title }}
        </router-link>
      </nav>
    </aside>

    <div class="admin-main">
      <header class="admin-header">
        <div>
          <strong>汽车充电桩可视化导览平台</strong>
          <span>{{ currentTitle }}</span>
        </div>
        <div class="user-actions">
          <span>{{ userStore.displayName }}</span>
          <router-link to="/login" class="text-action">切换账号</router-link>
        </div>
      </header>
      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { moduleMenus } from '../views/admin/adminModules'

const userStore = useUserStore()
const route = useRoute()
const currentTitle = computed(() => moduleMenus.find(item => route.path.endsWith(`/${item.key}`))?.title || '第三阶段管理工作台')
</script>
