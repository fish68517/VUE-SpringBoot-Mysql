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

  function open(tab: ViewTab) {
    const existing = tabs.value.find((item) => item.path === tab.path)
    if (existing) {
      existing.title = tab.title
      return
    }
    tabs.value.push(tab)
  }

  function add(route: RouteLocationNormalizedLoaded) {
    if (!route.meta.title || route.path === '/login') return
    open({
      path: route.path,
      title: String(route.meta.title),
      closable: route.path !== '/dashboard',
    })
  }

  function remove(path: string) {
    const index = tabs.value.findIndex((item) => item.path === path)
    if (index > -1 && tabs.value[index]?.closable) tabs.value.splice(index, 1)
  }

  function closeOthers(path: string) {
    tabs.value = tabs.value.filter((item) => !item.closable || item.path === path)
  }

  function closeAll() {
    tabs.value = tabs.value.filter((item) => !item.closable)
  }

  return { tabs, open, add, remove, closeOthers, closeAll }
})
