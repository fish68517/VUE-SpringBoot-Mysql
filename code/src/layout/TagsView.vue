<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, ArrowRight, RefreshRight } from '@element-plus/icons-vue'
import { useTabsStore, type ViewTab } from '@/stores/tabs'

const route = useRoute()
const router = useRouter()
const tabsStore = useTabsStore()
const contextMenuVisible = ref(false)
const contextMenuX = ref(0)
const contextMenuY = ref(0)
const contextTab = ref<ViewTab | null>(null)

watch(() => route.fullPath, () => tabsStore.add(route), { immediate: true })

function close(path: string) {
  const active = route.path === path
  const index = tabsStore.tabs.findIndex((item) => item.path === path)
  tabsStore.remove(path)
  if (active) router.push(tabsStore.tabs[Math.max(index - 1, 0)]?.path || '/dashboard')
}

function refresh() {
  window.location.reload()
}

function openContextMenu(event: MouseEvent, tab: ViewTab) {
  const menuWidth = 136
  const menuHeight = 126
  contextTab.value = tab
  contextMenuX.value = Math.min(event.clientX, window.innerWidth - menuWidth - 8)
  contextMenuY.value = Math.min(event.clientY, window.innerHeight - menuHeight - 8)
  contextMenuVisible.value = true
}

function hideContextMenu() {
  contextMenuVisible.value = false
}

function closeCurrent() {
  const tab = contextTab.value
  if (!tab?.closable) return
  close(tab.path)
  hideContextMenu()
}

function closeOthers() {
  const tab = contextTab.value
  if (!tab) return
  tabsStore.closeOthers(tab.path)
  if (!tabsStore.tabs.some((item) => item.path === route.path)) router.push(tab.path)
  hideContextMenu()
}

function closeAll() {
  tabsStore.closeAll()
  if (route.path !== '/dashboard') router.push('/dashboard')
  hideContextMenu()
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') hideContextMenu()
}

onMounted(() => {
  document.addEventListener('click', hideContextMenu)
  document.addEventListener('keydown', handleKeydown)
  window.addEventListener('resize', hideContextMenu)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', hideContextMenu)
  document.removeEventListener('keydown', handleKeydown)
  window.removeEventListener('resize', hideContextMenu)
})
</script>

<template>
  <div class="tags-view">
    <button class="strip-action" type="button"><el-icon><ArrowLeft /></el-icon></button>
    <div class="tag-list">
      <button
        v-for="tab in tabsStore.tabs"
        :key="tab.path"
        class="view-tag"
        :class="{ active: route.path === tab.path }"
        type="button"
        @click="router.push(tab.path)"
        @contextmenu.prevent="openContextMenu($event, tab)"
      >
        <span>{{ tab.title }}</span>
        <i v-if="tab.closable" @click.stop="close(tab.path)">×</i>
      </button>
    </div>
    <button class="strip-action" type="button"><el-icon><ArrowRight /></el-icon></button>
    <button class="strip-action" type="button" title="刷新" @click="refresh"><el-icon><RefreshRight /></el-icon></button>

    <transition name="context-menu">
      <div
        v-if="contextMenuVisible"
        class="tab-context-menu"
        :style="{ left: `${contextMenuX}px`, top: `${contextMenuY}px` }"
        role="menu"
        @click.stop
      >
        <button
          type="button"
          role="menuitem"
          :disabled="!contextTab?.closable"
          @click="closeCurrent"
        >
          <span>×</span>关闭当前
        </button>
        <button type="button" role="menuitem" @click="closeOthers">
          <span>◫</span>关闭其他
        </button>
        <button type="button" role="menuitem" @click="closeAll">
          <span>⊠</span>关闭全部
        </button>
      </div>
    </transition>
  </div>
</template>

<style scoped lang="scss">
.tags-view {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 46px;
  padding: 0 13px;
  background: #fff;
  border-bottom: 1px solid #eceef2;
}

.tag-list {
  min-width: 0;
  flex: 1;
  display: flex;
  gap: 7px;
  overflow: hidden;
}

.strip-action,
.view-tag {
  border: 1px solid #e4e7ec;
  background: #fff;
  color: #78808d;
  cursor: pointer;
}

.strip-action {
  width: 29px;
  height: 29px;
  display: grid;
  place-items: center;
  border-color: transparent;
  border-radius: 5px;

  &:hover { color: var(--brand); background: var(--brand-light); }
}

.view-tag {
  height: 29px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 11px;
  border-radius: 4px;
  white-space: nowrap;
  font-size: 12px;

  &.active {
    border-color: #f0c1aa;
    background: #fff7f2;
    color: var(--brand);
  }

  i { font-style: normal; font-size: 15px; line-height: 1; }
}

.tab-context-menu {
  position: fixed;
  z-index: 3000;
  width: 136px;
  padding: 6px;
  border: 1px solid #e5e8ed;
  border-radius: 6px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(31, 38, 51, 0.16);

  button {
    width: 100%;
    height: 36px;
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 0 11px;
    border: 0;
    border-radius: 4px;
    background: transparent;
    color: #4c5563;
    font-size: 12px;
    text-align: left;
    cursor: pointer;

    span {
      width: 14px;
      color: #8b93a0;
      font-size: 15px;
      text-align: center;
    }

    &:hover:not(:disabled) {
      background: #fff3ec;
      color: var(--brand);

      span { color: var(--brand); }
    }

    &:disabled {
      color: #b9bec7;
      cursor: not-allowed;
      opacity: 0.7;
    }
  }
}

.context-menu-enter-active,
.context-menu-leave-active {
  transition: opacity 0.12s ease, transform 0.12s ease;
  transform-origin: top left;
}

.context-menu-enter-from,
.context-menu-leave-to {
  opacity: 0;
  transform: scale(0.96);
}
</style>
