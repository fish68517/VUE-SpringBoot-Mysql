<script setup lang="ts">
import { watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, ArrowRight, RefreshRight } from '@element-plus/icons-vue'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const router = useRouter()
const tabsStore = useTabsStore()

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
      >
        <span>{{ tab.title }}</span>
        <i v-if="tab.closable" @click.stop="close(tab.path)">×</i>
      </button>
    </div>
    <button class="strip-action" type="button"><el-icon><ArrowRight /></el-icon></button>
    <button class="strip-action" type="button" title="刷新" @click="refresh"><el-icon><RefreshRight /></el-icon></button>
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
</style>
