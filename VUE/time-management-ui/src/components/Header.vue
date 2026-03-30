<template>
  <div class="header-container">
    <div class="title">大学生时间管理系统</div>

    <div class="user-info">
      <el-dropdown @command="handleCommand">
        <span class="dropdown-link">
          欢迎您，{{ displayName }}
          <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="settings">个性化设置</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>

  <el-dialog
    v-model="profileDialogVisible"
    title="管理员信息"
    width="420px"
    destroy-on-close
  >
    <div class="profile-panel" v-loading="profileLoading">
      <div class="profile-top">
        <el-avatar :size="64" :src="userInfo.campusAvatarUrl">
          {{ avatarFallback }}
        </el-avatar>
        <div class="profile-main">
          <div class="profile-name">{{ displayName }}</div>
          <div class="profile-role">{{ roleLabel }}</div>
        </div>
      </div>

      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户ID">
          {{ userInfo.campusUserId || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="昵称">
          {{ userInfo.campusNickname || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ userInfo.campusEmailAddr || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="学号/工号">
          {{ userInfo.campusSchoolId || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          {{ statusLabel }}
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </el-dialog>

  <el-dialog
    v-model="settingsDialogVisible"
    title="个性化设置"
    width="460px"
    destroy-on-close
  >
    <div class="settings-panel">
      <div class="setting-block">
        <div class="setting-title">主题切换</div>
        <div class="setting-desc">在浅色模式和深色模式之间切换，系统会自动记住你的选择。</div>
        <el-radio-group v-model="themeMode" class="theme-selector" @change="handleThemeChange">
          <el-radio-button label="light">浅色模式</el-radio-button>
          <el-radio-button label="dark">深色模式</el-radio-button>
        </el-radio-group>
      </div>

      <div class="theme-preview" :class="`preview-${themeMode}`">
        <div class="preview-chip">当前主题</div>
        <div class="preview-name">{{ themeMode === 'dark' ? '深色模式' : '浅色模式' }}</div>
        <div class="preview-copy">
          {{ themeMode === 'dark' ? '更适合夜间管理和低光环境。' : '更适合日间浏览和清晰阅读。' }}
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowDown } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import api from '../api/NetWorkApi';
import { clearStoredUser, getStoredUser, setStoredUser } from '../utils/auth';
import { applyTheme, getStoredTheme } from '../utils/theme';

const router = useRouter();
const profileDialogVisible = ref(false);
const settingsDialogVisible = ref(false);
const profileLoading = ref(false);
const themeMode = ref(getStoredTheme());
const userInfo = reactive({
  campusUserId: null,
  campusNickname: '',
  campusEmailAddr: '',
  campusSchoolId: '',
  campusUserType: '',
  campusAvatarUrl: '',
  campusStatusFlag: null,
});

const syncUserInfo = (payload = {}) => {
  Object.assign(userInfo, {
    campusUserId: payload.campusUserId ?? null,
    campusNickname: payload.campusNickname ?? '',
    campusEmailAddr: payload.campusEmailAddr ?? '',
    campusSchoolId: payload.campusSchoolId ?? '',
    campusUserType: payload.campusUserType ?? '',
    campusAvatarUrl: payload.campusAvatarUrl ?? '',
    campusStatusFlag: payload.campusStatusFlag ?? null,
  });
};

syncUserInfo(getStoredUser() || {});

const displayName = computed(() => {
  return userInfo.campusNickname || userInfo.campusEmailAddr || '管理员';
});

const avatarFallback = computed(() => {
  return displayName.value.slice(0, 1).toUpperCase();
});

const roleLabel = computed(() => {
  return userInfo.campusUserType === 'admin' ? '管理员' : '用户';
});

const statusLabel = computed(() => {
  switch (userInfo.campusStatusFlag) {
    case 0:
      return '待审核';
    case 1:
      return '正常';
    case 2:
      return '禁用';
    default:
      return '-';
  }
});

const openProfileDialog = async () => {
  profileDialogVisible.value = true;
  if (!userInfo.campusUserId) {
    return;
  }

  profileLoading.value = true;
  try {
    const response = await api.campusUserApi.getById(userInfo.campusUserId);
    const latestUser = response.data?.data || response.data || {};
    syncUserInfo(setStoredUser(latestUser) || latestUser);
  } catch (error) {
    ElMessage.error('管理员信息加载失败');
  } finally {
    profileLoading.value = false;
  }
};

const openSettingsDialog = () => {
  themeMode.value = getStoredTheme();
  settingsDialogVisible.value = true;
};

const handleThemeChange = (theme) => {
  const nextTheme = applyTheme(theme);
  themeMode.value = nextTheme;
  ElMessage.success(`已切换为${nextTheme === 'dark' ? '深色' : '浅色'}模式`);
};

const logout = async () => {
  try {
    await ElMessageBox.confirm('确认退出当前登录状态吗？', '退出登录', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }

  clearStoredUser();
  syncUserInfo({});
  router.replace('/login');
  ElMessage.success('已退出登录');
};

const handleCommand = async (command) => {
  if (command === 'profile') {
    await openProfileDialog();
    return;
  }

  if (command === 'settings') {
    openSettingsDialog();
    return;
  }

  if (command === 'logout') {
    await logout();
  }
};
</script>

<style scoped>
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: var(--app-text-color);
}

.user-info {
  cursor: pointer;
}

.dropdown-link {
  display: inline-flex;
  align-items: center;
  color: var(--app-text-color);
  outline: none;
}

.profile-panel {
  min-height: 220px;
}

.profile-top {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.profile-name,
.setting-title,
.preview-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--app-text-color);
}

.profile-role,
.setting-desc,
.preview-copy {
  color: var(--app-text-secondary);
}

.settings-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.setting-block {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.theme-selector {
  width: fit-content;
}

.theme-preview {
  padding: 20px;
  border-radius: 18px;
  border: 1px solid var(--app-border-color);
  background: var(--app-surface-muted);
  transition: all 0.25s ease;
}

.preview-light {
  background: linear-gradient(135deg, #f8fbff 0%, #e9f1fb 100%);
}

.preview-dark {
  background: linear-gradient(135deg, #182231 0%, #0f1724 100%);
}

.preview-chip {
  display: inline-flex;
  align-items: center;
  margin-bottom: 10px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
  color: inherit;
  font-size: 12px;
}

.preview-dark .preview-name,
.preview-dark .preview-copy,
.preview-dark .preview-chip {
  color: #e5edf7;
}
</style>
