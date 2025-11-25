好的，这是汉化后的 “个人资料” (User Profile) 页面代码。

主要改动：

表单与展示文本：Username -> 用户名, Role -> 角色, Gender -> 性别, Intro -> 个人简介 等。

按钮文案：Edit Profile -> 编辑资料, Save -> 保存, Change Avatar -> 更换头像 等。

性别选项：Male/Female/Other -> 男/女/其他。

统计信息：Total Check-ins -> 累计打卡 等。

日期格式：改为 zh-CN 中文显示。

反馈提示：上传成功、更新成功等消息已翻译。

请复制以下代码覆盖：

code
Html
play_circle
download
content_copy
expand_less
<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人资料</span>
          <el-button v-if="!isEditing" type="primary" @click="startEdit">编辑资料</el-button>
        </div>
      </template>

      <!-- 展示模式 (Display Mode) -->
      <div v-if="!isEditing" class="profile-display">
        <div class="profile-avatar-section">
          <el-avatar :size="120" :src="profile.avatar || '/default-avatar.png'" />
        </div>
        
        <div class="profile-info">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ formatRole(profile.role) }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ formatGender(profile.gender) }}</el-descriptions-item>
            <el-descriptions-item label="个人简介">{{ profile.intro || '暂无简介' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDate(profile.createdAt) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 打卡统计 (Check-in Statistics) -->
        <div class="checkin-stats">
          <h3>打卡统计</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic title="累计打卡" :value="checkInStats.totalCount || 0" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="当前连续" :value="checkInStats.currentStreak || 0">
                <template #suffix>天</template>
              </el-statistic>
            </el-col>
            <el-col :span="8">
              <el-statistic title="历史最长" :value="checkInStats.longestStreak || 0">
                <template #suffix>天</template>
              </el-statistic>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 编辑模式 (Edit Mode) -->
      <div v-else class="profile-edit">
        <el-form :model="editForm" :rules="rules" ref="profileForm" label-width="120px">
          <el-form-item label="头像">
            <div class="avatar-upload-section">
              <el-avatar :size="120" :src="avatarPreview || profile.avatar || '/default-avatar.png'" />
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :http-request="handleAvatarUpload"
                accept="image/jpeg,image/png,image/gif"
              >
                <el-button type="primary" size="small">更换头像</el-button>
              </el-upload>
            </div>
          </el-form-item>

          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="editForm.gender">
              <el-radio label="Male">男</el-radio>
              <el-radio label="Female">女</el-radio>
              <el-radio label="Other">其他</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="个人简介" prop="intro">
            <el-input
              v-model="editForm.intro"
              type="textarea"
              :rows="4"
              placeholder="介绍一下你自己..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
            <el-button @click="cancelEdit">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
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
    { max: 500, message: '简介不能超过500字', trigger: 'blur' }
  ]
};

// Fetch profile data
const fetchProfile = async () => {
  try {
    const data = await getProfile();
    profile.value = data;
  } catch (error) {
    // Error already handled by request interceptor
    console.error('加载个人资料失败:', error);
  }
};

// Fetch check-in statistics
const fetchCheckInStats = async () => {
  try {
    const data = await getCheckInStats();
    checkInStats.value = data;
  } catch (error) {
    console.error('加载打卡统计失败:', error);
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
    showError('头像必须是 JPG, PNG 或 GIF 格式');
    return false;
  }
  if (!isLt5M) {
    showError('头像大小不能超过 5MB');
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
    showSuccess('头像上传成功');
  } catch (error) {
    // Error already handled by request interceptor
    console.error('头像上传失败:', error);
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
        successMessage: '资料更新成功',
        errorMessage: '资料更新失败'
      }
    );
  } catch (error) {
    // Error already handled
  } finally {
    saving.value = false;
  }
};

// Helper functions for formatting
const formatGender = (gender) => {
  const map = {
    'Male': '男',
    'Female': '女',
    'Other': '其他'
  };
  return map[gender] || gender || '未设置';
};

const formatRole = (role) => {
  const map = {
    'user': '普通用户',
    'coach': '教练',
    'admin': '管理员'
  };
  return map[role] || role;
};

// Format date to Chinese locale
const formatDate = (dateString) => {
  if (!dateString) return '暂无';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { 
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
/* 样式保持不变 */
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