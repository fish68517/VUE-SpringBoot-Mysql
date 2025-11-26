<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span class="title">User Profile</span>
          <el-button
            v-if="!isEditing"
            type="primary"
            @click="toggleEditMode"
          >
            Edit Profile
          </el-button>
        </div>
      </template>

      <!-- Loading state -->
      <el-skeleton v-if="isLoading" :rows="5" animated />

      <!-- Display mode -->
      <div v-else-if="!isEditing" class="profile-display">
        <div class="profile-item">
          <span class="label">Username:</span>
          <span class="value">{{ user?.username }}</span>
        </div>
        <div class="profile-item">
          <span class="label">Email:</span>
          <span class="value">{{ user?.email }}</span>
        </div>
        <div class="profile-item">
          <span class="label">Avatar URL:</span>
          <span class="value">{{ user?.avatarUrl || 'Not set' }}</span>
        </div>
        <div class="profile-item">
          <span class="label">Bio:</span>
          <span class="value">{{ user?.bio || 'Not set' }}</span>
        </div>
        <div class="profile-item">
          <span class="label">Member Since:</span>
          <span class="value">{{ formatDate(user?.createdAt) }}</span>
        </div>
      </div>

      <!-- Edit mode -->
      <ProfileEditor
        v-else
        :user="user"
        @update="handleProfileUpdate"
        @cancel="toggleEditMode"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/userStore'
import { userService } from '../services/userService'
import ProfileEditor from '../components/ProfileEditor.vue'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const user = ref(null)
const isLoading = ref(false)
const isEditing = ref(false)

const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const loadUserProfile = async () => {
  try {
    isLoading.value = true
    const response = await userService.getUserById(userStore.user.id)
    user.value = response.data
  } catch (error) {
    const errorMessage = error.message || 'Failed to load profile'
    ElMessage.error(errorMessage)
  } finally {
    isLoading.value = false
  }
}

const toggleEditMode = () => {
  isEditing.value = !isEditing.value
}

const handleProfileUpdate = (updatedUser) => {
  user.value = updatedUser
  userStore.setUser(updatedUser)
  isEditing.value = false
  ElMessage.success('Profile updated successfully')
}

onMounted(() => {
  if (userStore.user?.id) {
    loadUserProfile()
  }
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.profile-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.profile-display {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.profile-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.profile-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: 600;
  color: #606266;
  min-width: 120px;
}

.value {
  color: #303133;
  word-break: break-all;
}
</style>
