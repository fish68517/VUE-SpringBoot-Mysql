<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>User Profile</span>
          <el-button v-if="!isEditing" type="primary" @click="startEdit">Edit Profile</el-button>
        </div>
      </template>

      <!-- Display Mode -->
      <div v-if="!isEditing" class="profile-display">
        <div class="profile-avatar-section">
          <el-avatar :size="120" :src="profile.avatar || '/default-avatar.png'" />
        </div>
        
        <div class="profile-info">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="Username">{{ profile.username }}</el-descriptions-item>
            <el-descriptions-item label="Role">{{ profile.role }}</el-descriptions-item>
            <el-descriptions-item label="Gender">{{ profile.gender || 'Not set' }}</el-descriptions-item>
            <el-descriptions-item label="Introduction">{{ profile.intro || 'No introduction yet' }}</el-descriptions-item>
            <el-descriptions-item label="Registration Date">{{ formatDate(profile.createdAt) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- Check-in Statistics -->
        <div class="checkin-stats">
          <h3>Check-in Statistics</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic title="Total Check-ins" :value="checkInStats.totalCount || 0" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="Current Streak" :value="checkInStats.currentStreak || 0">
                <template #suffix>days</template>
              </el-statistic>
            </el-col>
            <el-col :span="8">
              <el-statistic title="Longest Streak" :value="checkInStats.longestStreak || 0">
                <template #suffix>days</template>
              </el-statistic>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- Edit Mode -->
      <div v-else class="profile-edit">
        <el-form :model="editForm" :rules="rules" ref="profileForm" label-width="120px">
          <el-form-item label="Avatar">
            <div class="avatar-upload-section">
              <el-avatar :size="120" :src="avatarPreview || profile.avatar || '/default-avatar.png'" />
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :http-request="handleAvatarUpload"
                accept="image/jpeg,image/png,image/gif"
              >
                <el-button type="primary" size="small">Change Avatar</el-button>
              </el-upload>
            </div>
          </el-form-item>

          <el-form-item label="Gender" prop="gender">
            <el-radio-group v-model="editForm.gender">
              <el-radio label="Male">Male</el-radio>
              <el-radio label="Female">Female</el-radio>
              <el-radio label="Other">Other</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="Introduction" prop="intro">
            <el-input
              v-model="editForm.intro"
              type="textarea"
              :rows="4"
              placeholder="Tell us about yourself..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="saveProfile" :loading="saving">Save</el-button>
            <el-button @click="cancelEdit">Cancel</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getProfile, updateProfile, uploadAvatar } from '@/api/user';
import { getCheckInStats } from '@/api/checkin';
import { showSuccess, showError, showWarning, handleFormSubmit } from '@/utils/feedback';

const profile = ref({});
const checkInStats = ref({});
const isEditing = ref(false);
const saving = ref(false);
const avatarPreview = ref('');
const profileForm = ref(null);

const editForm = reactive({
  avatar: '',
  gender: '',
  intro: ''
});

const rules = {
  intro: [
    { max: 500, message: 'Introduction cannot exceed 500 characters', trigger: 'blur' }
  ]
};

// Fetch profile data
const fetchProfile = async () => {
  try {
    const data = await getProfile();
    profile.value = data;
  } catch (error) {
    // Error already handled by request interceptor
    console.error('Failed to load profile:', error);
  }
};

// Fetch check-in statistics
const fetchCheckInStats = async () => {
  try {
    const data = await getCheckInStats();
    checkInStats.value = data;
  } catch (error) {
    console.error('Failed to load check-in stats:', error);
    // Don't show error message as stats are optional
  }
};

// Start editing
const startEdit = () => {
  editForm.avatar = profile.value.avatar || '';
  editForm.gender = profile.value.gender || '';
  editForm.intro = profile.value.intro || '';
  avatarPreview.value = '';
  isEditing.value = true;
};

// Cancel editing
const cancelEdit = () => {
  isEditing.value = false;
  avatarPreview.value = '';
  if (profileForm.value) {
    profileForm.value.resetFields();
  }
};

// Validate avatar file before upload
const beforeAvatarUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type);
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    showError('Avatar must be JPG, PNG or GIF format');
    return false;
  }
  if (!isLt5M) {
    showError('Avatar size cannot exceed 5MB');
    return false;
  }
  return true;
};

// Handle avatar upload
const handleAvatarUpload = async ({ file }) => {
  try {
    const data = await uploadAvatar(file);
    editForm.avatar = data.url || data;
    avatarPreview.value = URL.createObjectURL(file);
    showSuccess('Avatar uploaded successfully');
  } catch (error) {
    // Error already handled by request interceptor
    console.error('Failed to upload avatar:', error);
  }
};

// Save profile
const saveProfile = async () => {
  if (!profileForm.value) return;
  
  saving.value = true;
  try {
    await handleFormSubmit(
      profileForm.value,
      async () => {
        const updateData = {
          avatar: editForm.avatar,
          gender: editForm.gender,
          intro: editForm.intro
        };
        
        await updateProfile(updateData);
        isEditing.value = false;
        avatarPreview.value = '';
        await fetchProfile();
      },
      {
        successMessage: 'Profile updated successfully',
        errorMessage: 'Failed to update profile'
      }
    );
  } catch (error) {
    // Error already handled
  } finally {
    saving.value = false;
  }
};

// Format date
const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  });
};

onMounted(() => {
  fetchProfile();
  fetchCheckInStats();
});
</script>

<style scoped>
.profile-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}

.profile-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.profile-display {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.profile-avatar-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.profile-info {
  margin-bottom: 20px;
}

.checkin-stats {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.checkin-stats h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #303133;
}

.profile-edit {
  padding: 20px 0;
}

.avatar-upload-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar-uploader {
  margin-top: 10px;
}

@media (max-width: 768px) {
  .profile-container {
    padding: 0 10px;
  }

  .card-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>
