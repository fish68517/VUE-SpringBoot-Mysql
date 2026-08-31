<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Bell, FullScreen, Search, User, SwitchButton, Setting } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const noticeVisible = ref(false)
const breadcrumbs = computed(() => route.matched.filter((item) => item.meta.title && item.path !== '/'))

async function logout() {
  await ElMessageBox.confirm('确定退出数字法治底座吗？', '退出确认', {
    confirmButtonText: '退出', cancelButtonText: '取消', type: 'warning',
  })
  await userStore.logout()
  await router.replace('/login')
}

function toggleFullscreen() {
  if (!document.fullscreenElement) document.documentElement.requestFullscreen()
  else document.exitFullscreen()
}

function search() {
  ElMessage.info('全局检索为前端 Mock 演示功能')
}
</script>

<template>
  <header class="header-bar">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
        {{ item.meta.title }}
      </el-breadcrumb-item>
    </el-breadcrumb>

    <div class="header-actions">
      <button class="icon-action" type="button" title="全局搜索" @click="search">
        <el-icon><Search /></el-icon>
      </button>
      <button class="icon-action" type="button" title="全屏" @click="toggleFullscreen">
        <el-icon><FullScreen /></el-icon>
      </button>
      <button class="icon-action has-badge" type="button" title="消息" @click="noticeVisible = true">
        <el-icon><Bell /></el-icon><i>6</i>
      </button>
      <span class="divider" />
      <el-dropdown trigger="click">
        <div class="user-entry">
          <div class="avatar">{{ userStore.displayName.slice(0, 1) }}</div>
          <div class="user-copy">
            <strong>{{ userStore.displayName }}</strong>
            <span>{{ userStore.user?.deptName || '系统管理部' }}</span>
          </div>
          <span class="chevron">⌄</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item :icon="User" @click="router.push('/profile')">个人中心</el-dropdown-item>
            <el-dropdown-item :icon="Setting" @click="router.push('/system/user')">系统设置</el-dropdown-item>
            <el-dropdown-item divided :icon="SwitchButton" @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>

  <el-drawer v-model="noticeVisible" title="消息通知" size="390px">
    <div class="notice-item" v-for="notice in [
      ['待办提醒', '您有 6 项事件待处理，请及时办理。', '10分钟前'],
      ['接口告警', '渝快政用户同步接口恢复正常。', '35分钟前'],
      ['系统通知', '本周数据权限复核任务已发布。', '2小时前'],
    ]" :key="notice[0]">
      <span class="notice-dot" />
      <div><strong>{{ notice[0] }}</strong><p>{{ notice[1] }}</p><small>{{ notice[2] }}</small></div>
    </div>
  </el-drawer>
</template>

<style scoped lang="scss">
.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 68px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #eceef2;
  box-shadow: 0 2px 10px rgba(36, 42, 55, 0.03);
  z-index: 2;
}

.breadcrumb {
  font-size: 13px;
}

.header-actions,
.user-entry {
  display: flex;
  align-items: center;
}

.header-actions {
  gap: 5px;
}

.icon-action {
  position: relative;
  width: 38px;
  height: 38px;
  border: 0;
  border-radius: 7px;
  background: transparent;
  color: #6a7280;
  font-size: 18px;
  cursor: pointer;

  &:hover { color: var(--brand); background: var(--brand-light); }

  i {
    position: absolute;
    right: 6px;
    top: 3px;
    min-width: 15px;
    height: 15px;
    padding: 0 3px;
    border: 2px solid #fff;
    border-radius: 8px;
    background: #ef5b61;
    color: #fff;
    font-size: 9px;
    font-style: normal;
    line-height: 11px;
  }
}

.divider {
  width: 1px;
  height: 26px;
  margin: 0 10px;
  background: #eceef2;
}

.user-entry {
  gap: 10px;
  padding: 5px 7px;
  border-radius: 8px;
  cursor: pointer;

  &:hover { background: #f7f8fa; }
}

.avatar {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #f19365, #c84d21);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
}

.user-copy {
  display: flex;
  flex-direction: column;
  gap: 1px;

  strong { color: #303644; font-size: 13px; }
  span { color: #9299a6; font-size: 11px; }
}

.chevron { color: #8d94a0; font-size: 16px; }

.notice-item {
  display: flex;
  gap: 12px;
  padding: 15px 2px;
  border-bottom: 1px solid #eef0f3;

  strong { font-size: 14px; color: #303644; }
  p { margin: 7px 0; color: #667080; font-size: 13px; line-height: 1.6; }
  small { color: #a0a6b0; }
}

.notice-dot {
  width: 8px;
  height: 8px;
  margin-top: 6px;
  border-radius: 50%;
  background: var(--brand);
}
</style>
