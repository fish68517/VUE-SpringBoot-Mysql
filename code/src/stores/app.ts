import { ref } from 'vue'
import { defineStore } from 'pinia'
import { storage } from '@/utils/storage'

export const useAppStore = defineStore('app', () => {
  const collapsed = ref(storage.get('digital-law-sidebar-collapsed', false))

  function toggleSidebar() {
    collapsed.value = !collapsed.value
    storage.set('digital-law-sidebar-collapsed', collapsed.value)
  }

  return { collapsed, toggleSidebar }
})
