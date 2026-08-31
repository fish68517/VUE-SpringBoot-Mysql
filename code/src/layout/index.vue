<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import HeaderBar from './Header.vue'
import Sidebar from './Sidebar.vue'
import TagsView from './TagsView.vue'
import AppMain from './AppMain.vue'
import { useAppStore } from '@/stores/app'
import { useTabsStore } from '@/stores/tabs'
import { useUserStore } from '@/stores/user'

const appStore = useAppStore()
const tabsStore = useTabsStore()
const userStore = useUserStore()
const route = useRoute()

onMounted(async () => {
  tabsStore.add(route)
  if (!userStore.loaded) await userStore.loadUser()
})
</script>

<template>
  <div class="app-layout" :class="{ collapsed: appStore.collapsed }">
    <Sidebar />
    <section class="layout-body">
      <HeaderBar />
      <TagsView />
      <AppMain />
    </section>
  </div>
</template>

<style scoped lang="scss">
.app-layout {
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: 236px minmax(0, 1fr);
  background: var(--canvas);
  transition: grid-template-columns 0.2s ease;
}

.app-layout.collapsed {
  grid-template-columns: 72px minmax(0, 1fr);
}

.layout-body {
  min-width: 0;
  height: 100%;
  display: grid;
  grid-template-rows: 68px 46px minmax(0, 1fr);
  overflow: hidden;
}
</style>
