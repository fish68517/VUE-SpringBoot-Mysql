<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>个人信息</span>
          </template>
          <div class="profile-info">
            <el-avatar :size="80" class="avatar">
              {{ (userStore.user?.nickname || userStore.user?.username || '').charAt(0) }}
            </el-avatar>
            <h3>{{ userStore.user?.nickname || userStore.user?.username }}</h3>
            <p class="username">@{{ userStore.user?.username }}</p>
            <el-tag v-if="userStore.user?.role === 1" type="danger">管理员</el-tag>
            <el-tag v-else>普通用户</el-tag>
          </div>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span>学习成就</span>
          </template>
          <div class="achievements">
            <div class="achievement-item">
              <div class="badge" :class="badgeClass">
                <span class="badge-icon">{{ badgeIcon }}</span>
              </div>
              <div class="badge-info">
                <div class="badge-name">{{ badgeName }}</div>
                <div class="badge-desc">本月学习 {{ formatTime(userStore.user?.monthlyStudyTime || 0) }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>
            <span>修改信息</span>
          </template>
          <el-form :model="form" label-width="100px" style="max-width: 500px">
            <el-form-item label="用户名">
              <el-input :value="userStore.user?.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateInfo" :loading="updating">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form :model="passwordForm" label-width="100px" style="max-width: 500px">
            <el-form-item label="原密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请确认新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword" :loading="changingPassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '../store/user'
import { updateUser, getUserInfo } from '../api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const updating = ref(false)
const changingPassword = ref(false)

const form = reactive({
  nickname: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const formatTime = (minutes) => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}小时${mins}分钟`
}

const monthlyHours = computed(() => (userStore.user?.monthlyStudyTime || 0) / 60)

const badgeClass = computed(() => {
  if (monthlyHours.value >= 100) return 'diamond'
  if (monthlyHours.value >= 60) return 'gold'
  if (monthlyHours.value >= 30) return 'silver'
  if (monthlyHours.value >= 10) return 'bronze'
  return 'none'
})

const badgeIcon = computed(() => {
  if (monthlyHours.value >= 100) return '💎'
  if (monthlyHours.value >= 60) return '🥇'
  if (monthlyHours.value >= 30) return '🥈'
  if (monthlyHours.value >= 10) return '🥉'
  return '🎯'
})

const badgeName = computed(() => {
  if (monthlyHours.value >= 100) return '钻石学霸'
  if (monthlyHours.value >= 60) return '金牌学员'
  if (monthlyHours.value >= 30) return '银牌学员'
  if (monthlyHours.value >= 10) return '铜牌学员'
  return '努力中'
})

const initForm = () => {
  form.nickname = userStore.user?.nickname || ''
  form.phone = userStore.user?.phone || ''
  form.email = userStore.user?.email || ''
}

const handleUpdateInfo = async () => {
  updating.value = true
  try {
    await updateUser(form)
    const res = await getUserInfo()
    userStore.setUser(res.data)
    ElMessage.success('保存成功')
  } finally {
    updating.value = false
  }
}

const handleUpdatePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入密码不一致')
    return
  }
  changingPassword.value = true
  try {
    await updateUser({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  initForm()
})
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 0 auto;
  text-align: center;
}

.profile-info {
  text-align: center;
  padding: 20px;
}

.avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 32px;
  margin-bottom: 15px;
}

.profile-info h3 {
  margin: 10px 0 5px;
}

.username {
  color: #909399;
  margin-bottom: 10px;
}

.achievements {
  padding: 10px;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 15px;
  justify-content: center;
}

.badge {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-icon {
  font-size: 32px;
}

.badge.diamond { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.badge.gold { background: linear-gradient(135deg, #f5af19 0%, #f12711 100%); }
.badge.silver { background: linear-gradient(135deg, #bdc3c7 0%, #2c3e50 100%); }
.badge.bronze { background: linear-gradient(135deg, #c9a227 0%, #8b6914 100%); }
.badge.none { background: #f5f7fa; }

.badge-name {
  font-weight: bold;
  font-size: 16px;
}

.badge-desc {
  color: #909399;
  font-size: 14px;
}

.badge-info {
  text-align: center;
}
</style>
