import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { RouteLocationNormalizedLoaded } from 'vue-router'

export interface ViewTab {
  path: string
  title: string
  closable: boolean
}

export const useTabsStore = defineStore('tabs', () => {
  const tabs = ref<ViewTab[]>([{ path: '/dashboard', title: '工作台', closable: false }])

  function add(route: RouteLocationNormalizedLoaded) {
    if (!route.meta.title || route.path === '/login') return
    if (!tabs.value.some((item) => item.path === route.path)) {
      tabs.value.push({ path: route.path, title: String(route.meta.title), closable: true })
    }
  }

  function remove(path: string) {
    const index = tabs.value.findIndex((item) => item.path === path)
    if (index > -1 && tabs.value[index]?.closable) tabs.value.splice(index, 1)
  }

  return { tabs, add, remove }
})
