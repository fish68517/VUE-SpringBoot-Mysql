<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Fold, Expand } from '@element-plus/icons-vue'
import { routes } from '@/router/routes'
import { useAppStore } from '@/stores/app'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const tabsStore = useTabsStore()
const rootRoute = routes.find((item) => item.path === '/')
const menuRoutes = computed(() => rootRoute?.children?.filter((item) => item.meta?.menu) || [])

function toPath(parentPath: string, childPath: string) {
  return `/${parentPath}/${childPath}`.replace(/\/+/g, '/')
}

function toAbsolutePath(path: string) {
  return `/${path}`.replace(/\/+/g, '/')
}

function openMenu(path: string, title: unknown) {
  tabsStore.open({
    path,
    title: String(title || ''),
    closable: path !== '/dashboard',
  })

  if (route.path !== path) router.push(path)
}
</script>

<template>
  <aside class="sidebar">
    <div class="brand" @click="router.push('/dashboard')">
      <div class="brand-mark"><span>法</span></div>
      <div v-show="!appStore.collapsed" class="brand-copy">
        <strong>数字法治底座</strong>
        <small>DIGITAL RULE OF LAW</small>
      </div>
    </div>

    <el-scrollbar class="menu-scroll">
      <el-menu
        :default-active="route.path"
        :collapse="appStore.collapsed"
        :collapse-transition="false"
        background-color="transparent"
        text-color="#b9c0cd"
        active-text-color="#ffffff"
      >
        <template v-for="item in menuRoutes" :key="item.path">
          <el-sub-menu v-if="item.children?.length" :index="toAbsolutePath(item.path)">
            <template #title>
              <el-icon><component :is="item.meta?.icon" /></el-icon>
              <span>{{ item.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children.filter((node) => node.meta?.menu)"
              :key="child.path"
              :index="toPath(item.path, child.path)"
              @click="openMenu(toPath(item.path, child.path), child.meta?.title)"
            >
              <span class="submenu-dot" />
              <template #title>{{ child.meta?.title }}</template>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item
            v-else
            :index="toAbsolutePath(item.path)"
            @click="openMenu(toAbsolutePath(item.path), item.meta?.title)"
          >
            <el-icon><component :is="item.meta?.icon" /></el-icon>
            <template #title>{{ item.meta?.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>

    <button class="collapse-button" type="button" @click="appStore.toggleSidebar">
      <el-icon><component :is="appStore.collapsed ? Expand : Fold" /></el-icon>
      <span v-if="!appStore.collapsed">收起导航</span>
    </button>
  </aside>
</template>

<style scoped lang="scss">
.sidebar {
  height: 100%;
  min-width: 0;
  display: grid;
  grid-template-rows: 68px minmax(0, 1fr) 48px;
  background: linear-gradient(180deg, #282d38 0%, #20242d 100%);
  color: #fff;
  overflow: hidden;
  box-shadow: 2px 0 14px rgba(20, 25, 35, 0.08);
  z-index: 3;
}

.brand {
  height: 68px;
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 0 17px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
  cursor: pointer;
  white-space: nowrap;
}

.brand-mark {
  width: 38px;
  height: 38px;
  flex: 0 0 38px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 11px;
  background: linear-gradient(135deg, #ef7d43, #c84d21);
  box-shadow: 0 6px 18px rgba(216, 95, 43, 0.28);

  span {
    font-family: "STKaiti", "KaiTi", serif;
    font-size: 22px;
    font-weight: 700;
  }
}

.brand-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 1px;

  strong {
    font-size: 17px;
    letter-spacing: 0.5px;
  }

  small {
    color: #9098a8;
    font-size: 8px;
    letter-spacing: 1px;
  }
}

.menu-scroll {
  min-height: 0;
}

:deep(.el-menu) {
  border-right: none;
  padding: 13px 9px;
}

:deep(.el-menu--collapse) {
  width: 72px;
  padding-inline: 8px;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 48px;
  margin: 2px 0;
  border-radius: 6px;
  font-size: 14px;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.06) !important;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(216, 95, 43, 0.96), rgba(216, 95, 43, 0.72)) !important;
  box-shadow: 0 5px 14px rgba(216, 95, 43, 0.2);
}

:deep(.el-sub-menu .el-menu-item) {
  min-width: 0;
  padding-left: 46px !important;
}

.submenu-dot {
  width: 4px;
  height: 4px;
  margin-right: 10px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.7;
}

.collapse-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 9px;
  width: 100%;
  border: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.07);
  background: rgba(0, 0, 0, 0.08);
  color: #9ca4b3;
  cursor: pointer;

  &:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.04);
  }
}
</style>
