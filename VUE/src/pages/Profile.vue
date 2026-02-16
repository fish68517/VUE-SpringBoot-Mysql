<template>
  <div class="profile-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <!-- Profile Container -->
    <div class="profile-container">
      <div class="profile-header">
        <h1>个人中心</h1>
      </div>

      <div class="profile-content">
        <!-- Profile Form -->
        <div class="profile-form">
          <div class="form-section">
            <h2>个人信息</h2>

            <div class="form-group">
              <label>用户名</label>
              <input v-model="userInfo.username" type="text" disabled />
            </div>

            <div class="form-group">
              <label>邮箱</label>
              <input v-model="userInfo.email" type="email" disabled />
            </div>

            <div class="form-group">
              <label>昵称</label>
              <input v-model="userInfo.nickname" type="text" placeholder="输入昵称" />
            </div>

            <div class="form-group">
              <label>性别</label>
              <select v-model="userInfo.gender">
                <option value="">未指定</option>
                <option value="MALE">男</option>
                <option value="FEMALE">女</option>
                <option value="OTHER">其他</option>
              </select>
            </div>

            <div class="form-group">
              <label>生日</label>
              <input v-model="userInfo.birthday" type="date" />
            </div>

            <div class="form-group">
              <label>联系方式</label>
              <input v-model="userInfo.phone" type="tel" placeholder="输入手机号" />
            </div>

            <button @click="handleUpdateProfile" class="btn-save">保存个人信息</button>
          </div>

          <div class="form-section">
            <h2>修改密码</h2>

            <div class="form-group">
              <label>当前密码</label>
              <input v-model="passwordForm.oldPassword" type="password" placeholder="输入当前密码" />
            </div>

            <div class="form-group">
              <label>新密码</label>
              <input v-model="passwordForm.newPassword" type="password" placeholder="输入新密码" />
            </div>

            <div class="form-group">
              <label>确认新密码</label>
              <input v-model="passwordForm.confirmPassword" type="password" placeholder="确认新密码" />
            </div>

            <button @click="handleChangePassword" class="btn-save">修改密码</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import TopNavigation from '../components/TopNavigation.vue'
import http from '../utils/http'

// State
const userInfo = ref({
  username: '',
  email: '',
  nickname: '',
  gender: '',
  birthday: '',
  phone: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

/**
 * Fetch user profile
 */
const fetchUserProfile = async () => {
  try {
    const response = await http.get('/users/profile')
    userInfo.value = {
      username: response.data.username || '',
      email: response.data.email || '',
      nickname: response.data.nickname || '',
      gender: response.data.gender || '',
      birthday: response.data.birthday || '',
      phone: response.data.phone || ''
    }
  } catch (error) {
    ElMessage.error(error.message || '获取个人信息失败')
  }
}

/**
 * Handle update profile
 */
const handleUpdateProfile = async () => {
  try {
    const updateData = {
      nickname: userInfo.value.nickname,
      gender: userInfo.value.gender,
      birthday: userInfo.value.birthday,
      phone: userInfo.value.phone
    }

    await http.put('/users/profile', updateData)
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    ElMessage.error(error.message || '更新个人信息失败')
  }
}

/**
 * Handle change password
 */
const handleChangePassword = async () => {
  // Validate form
  if (!passwordForm.value.oldPassword) {
    ElMessage.error('请输入当前密码')
    return
  }

  if (!passwordForm.value.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }

  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  if (passwordForm.value.newPassword.length < 6) {
    ElMessage.error('新密码长度至少为6位')
    return
  }

  try {
    await http.put('/users/password', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })

    ElMessage.success('密码修改成功')
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    ElMessage.error(error.message || '修改密码失败')
  }
}

onMounted(() => {
  fetchUserProfile()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.profile-container {
  flex: 1;
  max-width: 1000px;
  margin: 0 auto;
  width: 100%;
  padding: 30px 20px;
}

.profile-header {
  background: white;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.profile-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 40px;
}

.form-section {
  padding-bottom: 30px;
  border-bottom: 1px solid var(--border-color);
}

.form-section:last-child {
  border-bottom: none;
}

.form-section h2 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 20px 0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 8px;
  font-size: 14px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.btn-save {
  padding: 10px 24px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.btn-save:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.btn-save:active {
  transform: scale(0.98);
}

/* Responsive */
@media (max-width: 768px) {
  .profile-container {
    padding: 20px 15px;
  }

  .profile-content {
    padding: 20px;
  }

  .profile-form {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .profile-header {
    padding: 20px;
  }

  .profile-header h1 {
    font-size: 24px;
  }
}
</style>
